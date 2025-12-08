package com.suraksha.app.presentation.map

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suraksha.app.domain.MapRepository
import com.suraksha.app.domain.model.AppLocation
import com.suraksha.app.domain.model.Rating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MapScreenVM @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _location = MutableStateFlow<AppLocation?>(null)
    val location = _location
    private val _address = MutableStateFlow<String?>(null)
    val address = _address.asStateFlow()
    private val _ratingSaved = MutableStateFlow(false)
    val ratingSaved = _ratingSaved.asStateFlow()

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            val loc = mapRepository.getCurrentLocation()
            Log.d("TAG", "fetchCurrentLocation: $loc")
            _location.value = loc
        }
    }

    fun fetchAddress(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val addr = mapRepository.fetchAddress(lat, lon)
            _address.value = addr ?: "Unable to fetch address"
            Log.d("TAG", "fetchAddress: ${addr}")
        }
    }

    fun saveLocationRating(rating: Rating, address: String){
        viewModelScope.launch {
            try {
                mapRepository.saveLocationRating(rating, address)
                _ratingSaved.value = true
            } catch (e: Exception) {
                Log.e("MapScreenVM", "Error saving rating", e)
            }
        }
    }

    fun resetRatingSaved() {
        _ratingSaved.value = false
    }
}