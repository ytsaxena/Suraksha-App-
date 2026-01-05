package com.suraksha_app.app.domain

import com.suraksha_app.app.domain.model.AppLocation
import com.suraksha_app.app.domain.model.PoliceStation
import com.suraksha_app.app.domain.model.Rating
import com.suraksha_app.app.domain.util.Error
import com.suraksha_app.app.domain.util.Result

interface MapRepository {
    suspend fun getCurrentLocation(): AppLocation?
    suspend fun fetchAddress(lat: Double, lon: Double): String?
    suspend fun saveLocationRating(rating: Rating, pincode: String)
    suspend fun getSafetyRating(pincode: String): Pair<Int, Int>

    suspend fun findNearbyPoliceStations(
        latitude: Double,
        longitude: Double,
        maxRadiusKm: Int,
    ): Result<List<PoliceStation>, Error>

}