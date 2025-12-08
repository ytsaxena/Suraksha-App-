package com.suraksha.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import com.suraksha.app.domain.MapRepository
import com.suraksha.app.domain.model.AppLocation
import com.suraksha.app.domain.model.Rating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapRepositoryImpl(
    private val context: Context,
    private val client: FusedLocationProviderClient,
    private val firestore: FirebaseFirestore
) : MapRepository {
    override suspend fun getCurrentLocation(): AppLocation? = suspendCoroutine { cont ->
        val hasPermissionFine = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasPermissionCoarse = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermissionFine && !hasPermissionCoarse) {
            cont.resume(null)
            return@suspendCoroutine
        }

        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        client.getCurrentLocation(request, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    cont.resume(AppLocation(loc.latitude, loc.longitude))
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener { cont.resume(null) }
    }

    override suspend fun fetchAddress(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val result = geocoder.getFromLocation(lat, lon, 1)
            val addr = result?.firstOrNull()?.getAddressLine(0)

            addr
                ?.split(",")                               // split parts
                ?.filterNot { it.trim().matches(Regex("^[2-9A-Z]{4}\\+[2-9A-Z]{2,3}$")) }
                ?.joinToString(",")                        // combine remaining parts
                ?.trim()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveLocationRating(rating: Rating, address: String) {
        try {
            val pincode = extractPincode(address)
                ?: throw IllegalArgumentException("No valid pincode found in address: $address")

            Log.d("SaveRating", "Extracted pincode: $pincode")

            val firestore = FirebaseFirestore.getInstance()
            val docRef = firestore.collection("pincode").document(pincode)

            firestore.runTransaction { transaction ->

                val snapshot = transaction.get(docRef)

                val safeCount = snapshot.getLong("safe") ?: 0
                val unsafeCount = snapshot.getLong("unsafe") ?: 0

                val updates = when (rating) {
                    Rating.SAFE -> mapOf(
                        "safe" to safeCount + 1,
                        "unsafe" to unsafeCount
                    )
                    Rating.UNSAFE -> mapOf(
                        "safe" to safeCount,
                        "unsafe" to unsafeCount + 1
                    )
                }

                Log.d("SaveRating", "Updating counts: $updates")

                transaction.set(docRef, updates)
            }.await()
            Log.d("SaveRating", "Rating saved SUCCESSFULLY for pincode: $pincode")

        } catch (e: Exception) {
            Log.e("SaveRating", "FAILED to save rating: ${e.message}", e)
        }
    }

    fun extractPincode(address: String): String? {
        val regex = Regex("\\b\\d{6}\\b")
        val matches = regex.findAll(address).toList()

        return matches.lastOrNull()?.value
    }
}