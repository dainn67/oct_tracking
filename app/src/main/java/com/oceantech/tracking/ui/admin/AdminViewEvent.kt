package com.oceantech.tracking.ui.admin

import com.oceantech.tracking.core.NimpeViewEvents

sealed class AdminViewEvent : NimpeViewEvents {
    object ResetLanguage : AdminViewEvent()
    object SaveFeedback : AdminViewEvent()
}