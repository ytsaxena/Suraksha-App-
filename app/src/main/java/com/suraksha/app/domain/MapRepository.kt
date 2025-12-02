package com.suraksha.app.domain

import com.suraksha.app.domain.model.AppLocation

interface MapRepository {
    suspend fun getCurrentLocation(): AppLocation?
    suspend fun fetchAddress(lat: Double, lon: Double): String?
}