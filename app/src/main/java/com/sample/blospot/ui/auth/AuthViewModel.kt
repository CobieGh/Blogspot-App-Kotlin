package com.sample.blospot.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sample.blospot.api.auth.network_responses.LoginResponse
import com.sample.blospot.repository.auth.AuthRepository
import com.sample.blospot.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : ViewModel() {

    fun testLogin(): LiveData<GenericApiResponse<LoginResponse>> {
        return authRepository.testLoginRequest(
            "jhonjherick.maravilla@gmail.com",
            "qwertyuiop.01"
        )
    }


}