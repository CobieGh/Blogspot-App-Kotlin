package com.sample.blospot.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.sample.blospot.R
import com.sample.blospot.ui.BaseActivity
import com.sample.blospot.ui.auth.state.AuthStateEvent
import com.sample.blospot.ui.main.MainActivity
import com.sample.blospot.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)

        subscibeObserver()
        checkPreviousAuthUser()
    }

    override fun expandAppBar() {
        // NO IMPLEMENTATION
    }

    fun checkPreviousAuthUser() {
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    private fun subscibeObserver() {
        // THIS IS FOR THE STATEEVENTS
        viewModel.dataState.observe(this, Observer { dataState ->

            onDataStateChange(dataState)

            dataState.data?.let { data ->

                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            Log.d(TAG, "subscibeObserver: AuthActivity, DataState: ${it}")
                            viewModel.setAuthToken(it)
                        }
                    }
                }
            }

        })

        viewModel.viewState.observe(this, Observer {
            it.authToken?.let {
                sessionManager.login(it)
            }
        })

        sessionManager.cachedToken.observe(this, Observer { authToken ->
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navMainActivity()
            }
        })
    }

    override fun displayProgressBar(boolean: Boolean) {
        if (boolean) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }
}