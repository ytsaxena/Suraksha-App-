package com.suraksha_app.app.presentation.sos.intro

import com.suraksha_app.app.domain.model.Contact

sealed interface SOSIntroNavEvent {
    data class NavigateToSOSScreen(val contactList: List<Contact>) : SOSIntroNavEvent
}