package com.oceantech.tracking.ui.security

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.response.TokenResponse
import com.oceantech.tracking.data.repository.AuthRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class SecurityViewModel @AssistedInject constructor(
    @Assisted state: SecurityViewState,
    val repository: AuthRepository,
    private val userRepo: UserRepository
) : TrackingViewModel<SecurityViewState, SecurityViewAction, SecurityViewEvent>(state) {
    @Inject
    lateinit var userPref: UserPreferences

    override fun handle(action: SecurityViewAction) {
        when (action) {
            is SecurityViewAction.LogginAction -> handleLogin(action.userName, action.password)
            is SecurityViewAction.SaveTokenAction -> handleSaveToken(action.token)
            is SecurityViewAction.CheckLogIn -> handleCheckLogin()
        }
    }

    private fun handleLogin(userName: String, password: String) {
        setState {
            copy(asyncToken = Loading())
        }

        repository.login(userName, password).execute {
            copy(asyncToken = it)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun handleCheckLogin() {
        setState { copy(asyncSession = Loading()) }

        GlobalScope.launch{
            //use coroutine to get token
            val token = userPref.accessToken.firstOrNull()

            userRepo.checkLogin(token).execute {
                copy(asyncSession = it)
            }
        }
    }

    private fun handleSaveToken(tokenResponse: TokenResponse) {
        this.viewModelScope.async {
            repository.saveAccessTokens(tokenResponse)
        }

    }

    fun handleReturnSignIn() {
        _viewEvents.post(SecurityViewEvent.ReturnSigninEvent)
    }

    fun handleReturnResetPass() {
        _viewEvents.post(SecurityViewEvent.ReturnResetpassEvent)
    }


    @AssistedFactory
    interface Factory {
        fun create(initialState: SecurityViewState): SecurityViewModel
    }

    companion object : MvRxViewModelFactory<SecurityViewModel, SecurityViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: SecurityViewState
        ): SecurityViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}