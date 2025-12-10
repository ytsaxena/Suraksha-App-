package com.suraksha.app.presentation.sos.selectContact

import com.suraksha.app.domain.model.Contact


sealed interface SelectContactNavEvent {
    data class NavigateToSOSScreen(val contactList: List<Contact>) : SelectContactNavEvent
}