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
import com.suraksha.app.domain.model.PoliceStation
import com.suraksha.app.domain.model.Rating
import com.suraksha.app.domain.util.Error
import com.suraksha.app.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapRepositoryImpl(
    private val context: Context,
    private val client: FusedLocationProviderClient,
    private val firestore: FirebaseFirestore,
    private val okHttpClient: OkHttpClient,
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

    override suspend fun saveLocationRating(rating: Rating, pincode: String) {
        try {
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

    override suspend fun getSafetyRating(pincode: String): Pair<Int, Int> {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val doc = firestore.collection("pincode").document(pincode).get().await()

            val safe = doc.getLong("safe")?.toInt() ?: 0
            val unsafe = doc.getLong("unsafe")?.toInt() ?: 0

            Pair(safe, unsafe)
        } catch (e: Exception) {
            Log.e("SafetyRepo", "Error fetching safety rating", e)
            Pair(0, 0)
        }
    }

    override suspend fun findNearbyPoliceStations(
        latitude: Double,
        longitude: Double,
        maxRadiusKm: Int,
    ): Result<List<PoliceStation>, Error> {
        val query = """
        [out:json][timeout:25];
        (
          node["amenity"="police"](around:${maxRadiusKm * 1000},$latitude,$longitude);
          way["amenity"="police"](around:${maxRadiusKm * 1000},$latitude,$longitude);
        );
        out center;
    """.trimIndent()

        val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())

        val request =
            Request.Builder().url("https://overpass-api.de/api/interpreter?data=$encodedQuery")
                .get().build()

        try {

            okHttpClient.newCall(request).execute().use { response ->

                return when (response.code) {
                    in 200..299 -> {
                        val body = response.body?.string() ?: return Result.Error(Error.UNKNOWN)

                        val jsonElement = Json.parseToJsonElement(body)
                        val elements =
                            jsonElement.jsonObject["elements"]?.jsonArray ?: return Result.Error(
                                Error.UNKNOWN
                            )

                        val stations = mutableListOf<PoliceStation>()

                        for (element in elements) {
                            val obj = element.jsonObject
                            val tags = obj["tags"]?.jsonObject

                            val lat = when {
                                obj.containsKey("lat") -> obj["lat"]?.jsonPrimitive?.doubleOrNull

                                obj.containsKey("center") -> obj["center"]?.jsonObject?.get("lat")?.jsonPrimitive?.doubleOrNull

                                else -> null
                            } ?: continue

                            val lon = when {
                                obj.containsKey("lon") -> obj["lon"]?.jsonPrimitive?.doubleOrNull
                                obj.containsKey("center") -> obj["center"]?.jsonObject?.get("lon")?.jsonPrimitive?.doubleOrNull
                                else -> null
                            } ?: continue

                            val id = obj["id"]?.jsonPrimitive?.longOrNull ?: continue

                            val name = tags?.get("name")?.jsonPrimitive?.contentOrNull ?: tags?.get(
                                "operator"
                            )?.jsonPrimitive?.contentOrNull ?: "Police Station"

                            val distance = calculateDistance(latitude, longitude, lat, lon)

                            stations.add(
                                PoliceStation(
                                    id = id,
                                    name = name,
                                    latitude = lat,
                                    longitude = lon,
                                    distance = distance
                                )
                            )

                        }

                        val sortedStations = stations
                            .sortedBy { it.distance }
                            .take(5)
                            .map { station ->
                                station.copy(
                                    distance = String.format("%.2f", station.distance).toDouble()
                                )
                            }

                        Result.Success(sortedStations)
                    }

                    408 -> Result.Error(Error.REQUEST_TIMEOUT)
                    429 -> Result.Error(Error.TOO_MANY_REQUESTS)
                    in 500..599 -> Result.Error(Error.SERVER_ERROR)
                    else -> Result.Error(Error.UNKNOWN)
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(Error.UNKNOWN)
        }
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
    ): Double {
        val R = 6371.0 // Earth's radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }


}