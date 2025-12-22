package com.suraksha.app.domain

import com.suraksha.app.domain.model.AppLocation
import com.suraksha.app.domain.model.PoliceStation
import com.suraksha.app.domain.model.Rating
import com.suraksha.app.domain.util.Error
import com.suraksha.app.domain.util.Result

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