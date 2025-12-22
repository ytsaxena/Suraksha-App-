package com.suraksha.app.domain.model

data class PoliceStation(
    val id: Long? = null,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distance: Double? = null, // in km
)