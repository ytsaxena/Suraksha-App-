package com.suraksha.app.presentation.sos.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suraksha.app.domain.MapRepository
import com.suraksha.app.domain.SOSRepository
import com.suraksha.app.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SosViewModel @Inject constructor(
    val sosRepository: SOSRepository,
    val mapRepository: MapRepository,
) : ViewModel() {

    private val _smsState = MutableStateFlow<SmsState>(SmsState.Idle)
    val smsState: StateFlow<SmsState> = _smsState.asStateFlow()

    var contacts: List<Contact> = mutableListOf()

    fun loadContacts(contactsList: List<Contact>) {
        contacts = contactsList
    }

    fun shareLiveLocation(currentTime: String) {
        viewModelScope.launch {
            val location = mapRepository.getCurrentLocation()
            location?.let {
                val locationLink =
                    "https://www.google.com/maps/dir/?api=1&destination=${location.latitude},${location.longitude}"
                val body =
                    "SOS Alert\nI am in danger and need immediate help.\nPlease check my location and contact me as soon as possible.\nLocation Link: $locationLink \nTime: $currentTime\nPlease help immediately."
                _smsState.value = SmsState.Loading
                contacts.forEach { contacts ->
                    sendSms(contacts.phoneNumber, body)
                }
                _smsState.value = SmsState.Idle
            }
        }
    }


    suspend fun sendSms(phoneNumber: String, message: String) {
        if (phoneNumber.isEmpty() || message.isEmpty()) {
            _smsState.value = SmsState.Error("Phone number and message cannot be empty")
            return
        }

        _smsState.value = SmsState.Loading

        val result = sosRepository.sendSms(phoneNumber, message)
        delay(2000)
        _smsState.value = if (result.isSuccess) {
            SmsState.Success("SMS sent successfully!")
        } else {
            SmsState.Error("Failed to send SMS: ${result.exceptionOrNull()?.message}")
        }
    }
}


sealed class SmsState {
    object Idle : SmsState()
    object Loading : SmsState()
    data class Success(val message: String) : SmsState()
    data class Error(val message: String) : SmsState()
}

