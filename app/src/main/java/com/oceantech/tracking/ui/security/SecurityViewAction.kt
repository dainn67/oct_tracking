package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.response.TokenResponse

sealed class SecurityViewAction : NimpeViewModelAction {
    data class LogginAction(var userName: String, var password: String) : SecurityViewAction()
    data class SaveTokenAction(var token: TokenResponse) : SecurityViewAction()
    object CheckLogIn : SecurityViewAction()
}