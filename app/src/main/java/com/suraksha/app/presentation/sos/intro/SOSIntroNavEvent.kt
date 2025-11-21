package com.suraksha.app.presentation.sos.intro

sealed interface SOSIntroNavEvent {
    data object NavigateToSOSScreen: SOSIntroNavEvent
}