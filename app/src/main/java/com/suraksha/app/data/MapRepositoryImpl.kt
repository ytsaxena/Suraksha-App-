package com.suraksha.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.suraksha.app.domain.MapRepository
import com.suraksha.app.domain.model.AppLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapRepositoryImpl(
    private val context: Context,
    private val client: FusedLocationProviderClient
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
}