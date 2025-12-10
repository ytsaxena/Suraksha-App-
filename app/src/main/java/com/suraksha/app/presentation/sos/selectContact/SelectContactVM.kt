package com.suraksha.app.presentation.sos.selectContact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suraksha.app.domain.SOSRepository
import com.suraksha.app.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectContactVM @Inject constructor(
    private val sosRepository: SOSRepository
): ViewModel() {
    private val _contactsFlow = MutableStateFlow<List<Contact>>(emptyList())
    val contactsFlow = _contactsFlow.asStateFlow()

    var selectedContacts by mutableStateOf(setOf<Contact>())
        private set

    private val _events = MutableSharedFlow<SelectContactNavEvent>()
    val events = _events

    fun toggleSelection(contact: Contact, selected: Boolean) {
        selectedContacts =
            if (selected) selectedContacts + contact
            else selectedContacts - contact
    }

    fun fetchContacts() {
        viewModelScope.launch {
            val fetchedContacts = sosRepository.getContact()
            _contactsFlow.value = fetchedContacts.sortedBy { it.name }.distinct()
        }
    }

    fun loadSelectedContact(contact: Set<Contact>){
        selectedContacts = contact.sortedBy { it.name }.distinct().toSet()
    }

    fun onSaveSelectContactClicked(){
        viewModelScope.launch {
            sosRepository.saveContacts(selectedContacts.toList())
            _events.emit(SelectContactNavEvent.NavigateToSOSScreen(selectedContacts.toList()))
        }
    }
}