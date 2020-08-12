package com.sample.blospot.ui.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.sample.blospot.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseAuthFramgnet : DaggerFragment() {
    val TAG = "BaseAuthFragment"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.let {
            ViewModelProvider(it, providerFactory).get(AuthViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }
}