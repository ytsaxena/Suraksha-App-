package com.suraksha.app.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suraksha.app.domain.MapRepository
import com.suraksha.app.domain.model.AppLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenVM @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _location = MutableStateFlow<AppLocation?>(null)
    val location = _location

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            val loc = mapRepository.getCurrentLocation()
            Log.d("TAG", "fetchCurrentLocation: $loc")
            _location.value = loc
        }
    }
}