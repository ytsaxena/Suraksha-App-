package com.suraksha.app.presentation.sos.selectContact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val sosRepository: SOSRepository,
    private val savedStateHandle: SavedStateHandle,
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

    fun loadSelectedContact(){
        savedStateHandle.get<String>("contactList")?.let { json ->
            val token = object : TypeToken<List<Contact>>() {}.type
            val contactList: List<Contact>? = Gson().fromJson(json, token)
            selectedContacts = contactList?.toSet() ?: emptySet()
        }

    }

    fun onSaveSelectContactClicked(){
        viewModelScope.launch {
            sosRepository.saveContacts(selectedContacts.toList())
            _events.emit(SelectContactNavEvent.NavigateToSOSScreen(selectedContacts.toList()))
        }
    }
}