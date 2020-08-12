package com.sample.blospot.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.sample.blospot.models.AuthToken
import com.sample.blospot.ui.DataState
import com.sample.blospot.ui.Response
import com.sample.blospot.ui.ResponseType
import com.sample.blospot.ui.auth.state.AuthViewState
import com.sample.blospot.util.*
import com.sample.blospot.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.sample.blospot.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.sample.blospot.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.sample.blospot.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, ViewStateType>
    (
    isNetworkAvailable: Boolean,
    isNetworkRequest: Boolean

) {
    private val TAG = "NetworkBoundResource"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if (isNetworkRequest) {
            if (isNetworkAvailable) {
                Log.d(TAG, "HERE WITH: ")
                coroutineScope.launch {
                    // simulate a network delay
                    delay(Constants.TESTING_NETWORK_DELAY)

                    withContext(Main) {
                        // make network call
                        val apiResponse = createCall()
                        result.addSource(apiResponse) { response ->
                            result.removeSource(apiResponse)

                            coroutineScope.launch {
                                handleNetworkCall(response)
                            }
                        }
                    }
                }

                GlobalScope.launch(IO) {
                    delay(Constants.NETWORK_TIMEOUT)

                    if (!job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: JOB NETWORK ERROR")
                        job.cancel(CancellationException((UNABLE_TO_RESOLVE_HOST)))
                    }

                }

            } else {
                Log.d(TAG, "HERE WITHOUTINTERNET: ")
                onErrorReturn(
                    UNABLE_TODO_OPERATION_WO_INTERNET,
                    shouldUseDialog = true,
                    shouldUseToast = false
                )
            }
        } else {
            coroutineScope.launch {

                delay(Constants.TESTING_CACHE_DELAY)

                // View data from cache ONLY and return
                createCacheRequestAndReturn()
            }
        }


    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }

            is ApiErrorResponse -> {
                onErrorReturn(response.errorMessage, true, false)
            }

            is ApiEmptyResponse -> {
                onErrorReturn("HTTP 204, Returned nothing", true, false)
            }
        }

    }


    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(
        errorMessage: String? = null,
        shouldUseDialog: Boolean,
        shouldUseToast: Boolean
    ) {
        var message = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()

        if (message == null) {
            message = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(message)) {
            message = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }

        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }

        if (useDialog) {
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(
            DataState.error(
                response = Response(
                    message = message,
                    responseType = responseType
                )
            )
        )
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called...")

        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {

                    if (job.isCancelled) {
                        Log.d(TAG, "invoke: Job has been cancelled")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        Log.d(TAG, "invoke: Job has been completed")
                    }

                }
            })

        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun setJob(job: Job)



}