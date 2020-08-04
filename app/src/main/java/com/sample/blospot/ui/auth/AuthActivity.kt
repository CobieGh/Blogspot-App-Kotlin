package com.sample.blospot.ui.auth

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sample.blospot.R
import com.sample.blospot.ui.BaseActivity
import com.sample.blospot.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authViewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
    }
}