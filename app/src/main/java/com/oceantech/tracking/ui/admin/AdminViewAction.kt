package com.oceantech.tracking.ui.admin

import com.oceantech.tracking.core.NimpeViewModelAction

sealed class HomeViewAction:NimpeViewModelAction{
    object ResetLang:HomeViewAction()

}