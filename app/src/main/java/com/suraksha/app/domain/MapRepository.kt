package com.suraksha.app.domain

import com.suraksha.app.domain.model.AppLocation

interface MapRepository {
    suspend fun getCurrentLocation(): AppLocation?
}