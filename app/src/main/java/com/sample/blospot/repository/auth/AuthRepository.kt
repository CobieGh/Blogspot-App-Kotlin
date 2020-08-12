package com.sample.blospot.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.sample.blospot.api.auth.OpenApiAuthService
import com.sample.blospot.api.auth.network_responses.LoginResponse
import com.sample.blospot.api.auth.network_responses.RegistrationResponse
import com.sample.blospot.models.AccountProperties
import com.sample.blospot.models.AuthToken
import com.sample.blospot.persistence.AccountPropertiesDao
import com.sample.blospot.persistence.AuthTokenDao
import com.sample.blospot.repository.NetworkBoundResource
import com.sample.blospot.session.SessionManager
import com.sample.blospot.ui.DataState
import com.sample.blospot.ui.Response
import com.sample.blospot.ui.ResponseType
import com.sample.blospot.ui.auth.state.AuthViewState
import com.sample.blospot.ui.auth.state.LoginFields
import com.sample.blospot.ui.auth.state.RegistrationFields
import com.sample.blospot.util.*
import com.sample.blospot.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.sample.blospot.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPrefences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) {

    private val TAG = "AuthRepository"

    private var repositoryJob: Job? = null

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {

        val loginFieldErrors = LoginFields(email, password).isValidForLogin()

        if (!loginFieldErrors.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnnectedToTheInternet(),
            true
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //dont care about the result. Just insert if it exist b/c foreign key relationship with AuthToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                // return so it wont proceed to the code below this
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveauthenticatedUserToPrefs(response.body.email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                response.body.pk,
                                response.body.token
                            )
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            // NOT USED IN THIS CASE
            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldsErrors =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()

        if (!registrationFieldsErrors.equals(RegistrationFields.RegistrationError.none())) {
            return returnErrorResponse(registrationFieldsErrors, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnnectedToTheInternet(), true
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //dont care about the result. Just insert if it exist b/c foreign key relationship with AuthToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                // return so it wont proceed to the code below this
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }



                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                response.body.pk,
                                response.body.token
                            )
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override suspend fun createCacheRequestAndReturn() {
                TODO("Not yet implemented")
            }

        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {

        val previousAuthUserEmail: String? =
            sharedPrefences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail.isNullOrBlank()) {
            Log.d(TAG, "checkPreviousAuthUser: NO PREVIOUSLY AUTHENTICATED USER")
            return returnNoTokenFound()
        }
        return object : NetworkBoundResource<Void, AuthViewState>(
            sessionManager.isConnnectedToTheInternet(), false
        ) {
            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                    Log.d(
                        TAG,
                        "createCacheRequestAndReturn: searching fotr token ${accountProperties} "
                    )

                    accountProperties?.let {
                        if (accountProperties.pk > -1) {
                            authTokenDao.searchByPk(accountProperties.pk).let { authToken ->

                                authToken?.let {
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            )
                                        )
                                    )
                                    return
                                }

                            }
                        }
                    }
                    Log.d(TAG, "createCacheRequestAndReturn: authToken not found...")
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )

                }
            }

            // NOT USED
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {

            }

            // NOT USED
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()

                value = DataState.data(
                    data = null,
                    response = Response(
                        SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                        ResponseType.Dialog()
                    )
                )
            }
        }
    }

    private fun saveauthenticatedUserToPrefs(email: String) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        Log.d(TAG, "returnErrorResponse: ${errorMessage}")
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()


                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )
            }
        }
    }


    fun cancelActiveJobs() {
        Log.d(TAG, "cancelActiveJobs: Cancelling on-going jobs...")
        repositoryJob?.cancel()
    }


}