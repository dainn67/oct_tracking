package com.oceantech.tracking.ui.client.homeScreen

import com.oceantech.tracking.core.NimpeViewEvents

sealed class HomeViewEvent : NimpeViewEvents {
    object ResetLanguage : HomeViewEvent()
    object SaveFeedback : HomeViewEvent()
}