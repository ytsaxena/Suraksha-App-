package com.suraksha.app.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suraksha.app.domain.MapRepository
import com.suraksha.app.domain.model.AppLocation
import com.suraksha.app.domain.model.PoliceStation
import com.suraksha.app.domain.model.Rating
import com.suraksha.app.domain.util.Error
import com.suraksha.app.domain.util.onError
import com.suraksha.app.domain.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val _safePercent = MutableStateFlow<Int?>(null)
    val safePercent = _safePercent.asStateFlow()

    private val _unsafePercent = MutableStateFlow<Int?>(null)
    val unsafePercent = _unsafePercent.asStateFlow()

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
            fetchSafetyRating(extractPincode(addr ?: "") ?: "")
        }
    }

    fun saveLocationRating(rating: Rating, address: String){
        viewModelScope.launch {
            try {
                val pincode = extractPincode(address) ?: return@launch
                mapRepository.saveLocationRating(rating, pincode = pincode)
                _ratingSaved.value = true
                fetchSafetyRating(pincode)
            } catch (e: Exception) {
                Log.e("MapScreenVM", "Error saving rating", e)
            }
        }
    }

    fun resetRatingSaved() {
        _ratingSaved.value = false
    }

    fun fetchSafetyRating(pincode: String) {
        viewModelScope.launch {
            try {
                val (safeCount, unsafeCount) = mapRepository.getSafetyRating(pincode)
                val total = safeCount + unsafeCount

                if (total == 0) {
                    _safePercent.value = null
                    _unsafePercent.value = null
                } else {
                    _safePercent.value = (safeCount * 100) / total
                    _unsafePercent.value = (unsafeCount * 100) / total
                }

            } catch (e: Exception) {
                Log.e("SafetyVM", "Error fetching safety rating", e)
            }
        }
    }

    fun reloadSafetyRating() {
        val addr = _address.value ?: return
        val pin = extractPincode(addr) ?: return
        fetchSafetyRating(pin)
    }

    fun extractPincode(address: String): String? {
        val regex = Regex("\\b\\d{6}\\b")
        val matches = regex.findAll(address).toList()

        return matches.lastOrNull()?.value
    }

    fun findNearbyPoliceStations(
        latitude: Double,
        longitude: Double,
        radiusKm: Int,
        onSuccess: (List<PoliceStation>) -> Unit,
        onError: (Error) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = mapRepository.findNearbyPoliceStations(latitude, longitude, radiusKm)
            withContext(Dispatchers.Main) {
                res.onSuccess { onSuccess(it) }.onError { onError(it) }
            }
        }
    }

}