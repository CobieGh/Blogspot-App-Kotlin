package com.sample.blospot.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sample.blospot.R
import com.sample.blospot.models.AuthToken
import com.sample.blospot.ui.auth.state.AuthStateEvent
import com.sample.blospot.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFramgnet() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        login_button.setOnClickListener {
            login()
        }

    }

    private fun subscribeObservers() {
        // THIS IS FOR THE FIELD VALUES OR DATA; VIEWSTATE
        viewModel.viewState.removeObservers(viewLifecycleOwner)
        viewModel.viewState.observe(viewLifecycleOwner, Observer {

            it.loginFields?.let { loginFields ->

                loginFields.login_email?.let {
                    input_email.setText(it)
                }

                loginFields.login_password?.let {
                    input_password.setText(it)
                }
            }

        })
    }

    fun login() {
        viewModel.setStateEvent(
            AuthStateEvent.LoginAttemptEvent(
                input_email.text.toString(),
                input_password.text.toString()
            )

        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(input_email.text.toString(), input_password.text.toString())
        )
    }


}