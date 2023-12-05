package com.oceantech.tracking.ui.security

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.response.CheckTokenResponse
import com.oceantech.tracking.data.model.response.TokenResponse
import com.oceantech.tracking.data.model.response.User

data class SecurityViewState(
    var asyncToken: Async<TokenResponse> = Uninitialized,
    var userCurrent: Async<User> = Uninitialized,
    var asyncSession: Async<CheckTokenResponse> = Uninitialized
) : MvRxState {
//    fun isLoading() = asyncToken is Loading || asyncSession is Loading
}