package com.suraksha.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.suraksha.app.domain.MapRepository
import com.suraksha.app.domain.model.AppLocation
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
}