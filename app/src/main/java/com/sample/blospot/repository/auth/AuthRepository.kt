package com.sample.blospot.repository.auth

import androidx.lifecycle.LiveData
import com.sample.blospot.api.auth.OpenApiAuthService
import com.sample.blospot.api.auth.network_responses.LoginResponse
import com.sample.blospot.api.auth.network_responses.RegistrationResponse
import com.sample.blospot.persistence.AccountPropertiesDao
import com.sample.blospot.persistence.AuthTokenDao
import com.sample.blospot.session.SessionManager
import com.sample.blospot.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    fun testLoginRequest(email: String, password: String): LiveData<GenericApiResponse<LoginResponse>>{
        return openApiAuthService.login(email, password)
    }

    fun testRegistrationRequest(email: String, username: String, password: String, password2: String): LiveData<GenericApiResponse<RegistrationResponse>>{
        return openApiAuthService.register(email, username, password, password2)
    }
}