package com.suraksha.app.presentation.sos.selectContact

sealed interface SelectContactNavEvent {
    data object NavigateToSOSScreen: SelectContactNavEvent
}