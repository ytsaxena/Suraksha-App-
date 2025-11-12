package com.suraksha.app.presentation.sos

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suraksha.app.domain.SOSRepository
import com.suraksha.app.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SOSScreenVM @Inject constructor(
    private val sosRepository: SOSRepository
) : ViewModel() {
    private val _contactsFlow = MutableStateFlow<List<Contact>>(emptyList())
    val contactsFlow = _contactsFlow.asStateFlow()

    var contacts: List<Contact> = emptyList()
        private set

    fun fetchContacts() {
        viewModelScope.launch {
            val fetchedContacts = sosRepository.getContact()
            _contactsFlow.value = fetchedContacts
            contacts = _contactsFlow.first()
        }
    }
}