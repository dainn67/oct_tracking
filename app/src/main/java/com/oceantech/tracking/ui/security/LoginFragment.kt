package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentLoginBinding
import com.oceantech.tracking.ui.MainActivity
import javax.inject.Inject


class LoginFragment @Inject constructor() : TrackingBaseFragment<FragmentLoginBinding>() {
    private val viewModel: SecurityViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    lateinit var username: String
    lateinit var password: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        views.username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) views.usernameTil.error = null
                else views.usernameTil.error = getString(R.string.username_not_empty)
            }
        })

        views.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) views.passwordTil.error = null
                else views.passwordTil.error = getString(R.string.username_not_empty)
            }
        })

        views.loginSubmit.setOnClickListener {
            loginSubmit()
        }
        views.labelSigin.setOnClickListener {
            viewModel.handleReturnSignIn()
        }
        views.labelResetPassword.setOnClickListener {
            viewModel.handleReturnResetPass()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loginSubmit() {
        username = views.username.text.toString().trim()
        password = views.password.text.toString().trim()
        username = "ND122"
        password = "123456"
        if (username.isNullOrEmpty()) views.usernameTil.error = getString(R.string.username_not_empty)
        if (password.isNullOrEmpty()) views.passwordTil.error = getString(R.string.username_not_empty)
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty())
            viewModel.handle(SecurityViewAction.LogginAction(username, password))
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncToken) {
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Success -> {
                views.waitingView.visibility = View.GONE
                it.asyncToken.invoke()?.let { token ->
                    val sessionManager =
                        context?.let { it1 -> SessionManager(it1.applicationContext) }
                    token.accessToken?.let { it1 -> sessionManager!!.saveAuthToken(it1) }
                    token.refreshToken?.let { it1 -> sessionManager!!.saveAuthTokenRefresh(it1) }
                    viewModel.handle(SecurityViewAction.SaveTokenAction(token))
                }
                Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_LONG).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }

            is Fail -> {
                views.waitingView.visibility = View.GONE
                views.passwordTil.error = getString(R.string.login_fail)
            }
        }
        when (it.asyncSession){
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Fail -> views.waitingView.visibility = View.GONE
            is Success -> {
                views.waitingView.visibility = View.GONE
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
        }
    }
}