package com.suraksha_app.app.presentation.sos.selectContact

import com.suraksha_app.app.domain.model.Contact


sealed interface SelectContactNavEvent {
    data class NavigateToSOSScreen(val contactList: List<Contact>) : SelectContactNavEvent
}