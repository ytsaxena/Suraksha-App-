package com.suraksha.app.domain

import com.suraksha.app.domain.model.AppLocation
import com.suraksha.app.domain.model.Rating

interface MapRepository {
    suspend fun getCurrentLocation(): AppLocation?
    suspend fun fetchAddress(lat: Double, lon: Double): String?
    suspend fun saveLocationRating(rating: Rating, address: String)
}