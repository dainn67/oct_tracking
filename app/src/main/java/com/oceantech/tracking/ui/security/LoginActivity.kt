package com.oceantech.tracking.ui.security

import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.databinding.ActivityLoginBinding
import com.oceantech.tracking.utils.addFragmentToBackstack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginActivity : TrackingBaseActivity<ActivityLoginBinding>(), SecurityViewModel.Factory {

    private val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityviewmodelFactory: SecurityViewModel.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(views.root)
        supportFragmentManager.commit {
            add<LoginFragment>(R.id.frame_layout)
        }

        viewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }

        viewModel.handleCheckLogin()
    }

    private fun handleEvent(event: SecurityViewEvent) {
        when (event) {
            is SecurityViewEvent.ReturnSigninEvent -> {
                addFragmentToBackstack(R.id.frame_layout, SigninFragment::class.java)
            }

            is SecurityViewEvent.ReturnResetpassEvent -> {
                addFragmentToBackstack(R.id.frame_layout, ResetPasswordFragment::class.java)
            }

        }
    }

    override fun getBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityviewmodelFactory.create(initialState)
    }

}