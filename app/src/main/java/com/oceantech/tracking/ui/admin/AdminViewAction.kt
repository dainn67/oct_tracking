package com.oceantech.tracking.ui.admin

import com.oceantech.tracking.core.NimpeViewModelAction

sealed class AdminViewAction : NimpeViewModelAction {
    object ResetLang : AdminViewAction()

}