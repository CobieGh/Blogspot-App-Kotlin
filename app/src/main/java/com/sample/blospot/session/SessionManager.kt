package com.sample.blospot.session

import android.app.Application
import com.sample.blospot.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val applicationL: Application
) {

}