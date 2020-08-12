package com.sample.blospot.ui.auth

import androidx.lifecycle.LiveData
import com.sample.blospot.models.AuthToken
import com.sample.blospot.repository.auth.AuthRepository
import com.sample.blospot.ui.BaseViewModel
import com.sample.blospot.ui.DataState
import com.sample.blospot.ui.auth.state.AuthStateEvent
import com.sample.blospot.ui.auth.state.AuthStateEvent.*
import com.sample.blospot.ui.auth.state.AuthViewState
import com.sample.blospot.ui.auth.state.LoginFields
import com.sample.blospot.ui.auth.state.RegistrationFields
import com.sample.blospot.util.AbsentLiveData
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when (stateEvent) {
            is LoginAttemptEvent -> {
                return authRepository.attemptLogin(
                    stateEvent.email, stateEvent.password
                )
            }

            is RegisterAttemptEvent -> {
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }

            is CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }
        }
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setRegistrationFieds(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields) {
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    fun cancelActiveJobs() {
        authRepository.cancelActiveJobs()
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}