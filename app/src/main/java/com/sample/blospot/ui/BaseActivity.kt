package com.sample.blospot.ui

import android.util.Log
import com.sample.blospot.session.SessionManager
import com.sample.blospot.ui.ResponseType.*
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener {
    val TAG = "BaseActivity"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {

        dataState?.let {
            GlobalScope.launch(Main) {

                displayProgressBar(it.loading.isLoading)

                it.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }

                it.data?.let {
                    it.response?.let { responseEvent ->
                        handleStateResponse(responseEvent)
                    }
                }

            }
        }
    }

    private fun handleStateError(errorEvent: Event<StateError>) {
        errorEvent.getContentIfNotHandled()?.let {
            when (it.response.responseType) {
                is Dialog -> {
                    it.response.message?.let { message ->
                        displayErrorDialog(message)
                    }
                }

                is Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                    }
                }

                is None -> {
                    Log.d(TAG, "handleStateError: ${it.response.message}")
                }
            }
        }
    }

    private fun handleStateResponse(event: Event<Response>) {
        event.getContentIfNotHandled()?.let {
            when (it.responseType) {
                is Dialog -> {
                    it.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }

                is Toast -> {
                    it.message?.let { message ->
                        displayToast(message)
                    }
                }

                is None -> {
                    Log.d(TAG, "handleStateError: ${it.message}")
                }
            }
        }
    }

    abstract fun displayProgressBar(boolean: Boolean)


}