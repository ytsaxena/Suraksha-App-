package com.suraksha.app.presentation.sos.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SosViewModel @Inject constructor() : ViewModel() {

    var sharingLocationStatus = MutableStateFlow(false)
        private set

    fun shareLiveLocation() {
        viewModelScope.launch {
            sharingLocationStatus.value = true
            delay(5000) // Simulating the Live Sharing
            sharingLocationStatus.value = false
        }
    }

}

