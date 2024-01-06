package com.oceantech.tracking.ui.client.homeScreen

import com.oceantech.tracking.core.NimpeViewModelAction

sealed class HomeViewAction:NimpeViewModelAction{
    object ResetLang: HomeViewAction()

}