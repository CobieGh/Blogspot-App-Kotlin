package com.sample.blospot.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.blospot.models.AuthToken
import com.sample.blospot.persistence.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
    private val TAG = "SessionManager"

    private val _cachedToken = MutableLiveData<AuthToken>()

    val cachedToken: LiveData<AuthToken>
        get() = _cachedToken


    // LOGIN FUNCTION
    fun login(newValue: AuthToken) {
        setValue(newValue)
    }

    fun setValue(newValue: AuthToken?) {
        GlobalScope.launch(Main) {
            if (_cachedToken.value != newValue) {
                _cachedToken.value = newValue
            }
        }
    }


    // LOGOUT FUNCTION
    fun logout() {
        GlobalScope.launch(IO) {
            var errorMessage: String? = null

            try {
                cachedToken.value!!.account_pk?.let {
                    authTokenDao.nullifyToken(it)
                }

            } catch (e: CancellationException) {
                Log.d(TAG, "logout: ${e.message}")
                errorMessage = e.message
            } catch (e: Exception) {
                Log.d(TAG, "logout: ${e.message}")
                errorMessage = errorMessage + "\n" + e.message
            } finally {
                errorMessage?.let {
                    Log.d(TAG, "logout: ${errorMessage}")
                }
                Log.d(TAG, "logout: FINALLY...")
                setValue(null)
            }
        }
    }

    fun isConnnectedToTheInternet(): Boolean {
        var result = false
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

}