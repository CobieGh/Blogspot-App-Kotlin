package com.sample.blospot.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sample.blospot.R
import kotlinx.android.synthetic.main.fragment_launcher.*

class LauncherFragment : BaseAuthFramgnet() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launcher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login.setOnClickListener {
            navLogin();
        }

        register.setOnClickListener {
            navRegister()
        }

        forgot_password.setOnClickListener {
            navForgotPassword()
        }
    }

    private fun navLogin() {
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    private fun navRegister() {
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    private fun navForgotPassword() {
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }


}