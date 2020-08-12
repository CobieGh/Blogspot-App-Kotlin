package com.sample.blospot.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sample.blospot.R
import com.sample.blospot.ui.auth.state.AuthStateEvent
import com.sample.blospot.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : BaseAuthFramgnet() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        register_button.setOnClickListener {
            register()
        }
    }

    private fun subscribeObservers() {
        // THIS IS FOR THE STATEEVENTS

        // THIS IS FOR THE FIELD VALUES OR DATA; VIEWSTATE
        viewModel.viewState.removeObservers(viewLifecycleOwner)
        viewModel.viewState.observe(viewLifecycleOwner, Observer {

            it.registrationFields?.let { registrationFields ->

                registrationFields.registration_email?.let {
                    input_email.setText(it)
                }

                registrationFields.registration_username?.let {
                    input_username.setText(it)
                }

                registrationFields.registration_passsword?.let {
                    input_password.setText(it)
                }

                registrationFields.registration_confirm_password?.let {
                    input_password_confirm.setText(it)
                }
            }

        })
    }

    private fun register() {
        viewModel.setStateEvent(
            AuthStateEvent.RegisterAttemptEvent(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.setRegistrationFieds(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }


}