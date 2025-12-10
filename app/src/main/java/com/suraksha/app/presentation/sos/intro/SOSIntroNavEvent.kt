package com.suraksha.app.presentation.sos.intro

import com.suraksha.app.domain.model.Contact

sealed interface SOSIntroNavEvent {
    data class NavigateToSOSScreen(val contactList: List<Contact>) : SOSIntroNavEvent
}