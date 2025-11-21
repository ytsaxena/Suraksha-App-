package com.suraksha.app.presentation.sos.intro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suraksha.app.domain.SOSRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SOSIntroScreenVM @Inject constructor(
    private val sosRepository: SOSRepository
) : ViewModel() {

    var isChecking by mutableStateOf(true)
        private set

    private val _events = MutableSharedFlow<SOSIntroNavEvent>()
    val events = _events

    init {
        checkSelectedContacts()
    }

    private fun checkSelectedContacts() {
        viewModelScope.launch {
            val contacts = sosRepository.selectedContacts()

            if (contacts.isNotEmpty()) {
                _events.emit(SOSIntroNavEvent.NavigateToSOSScreen)
                return@launch
            }

            isChecking = false
        }
    }
}