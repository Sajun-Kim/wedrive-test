package com.namuplanet.base.platfrom

import androidx.lifecycle.MutableLiveData
import com.namuplanet.base.data.Failure
import com.namuplanet.base.data.handler.awaitResult
import com.namuplanet.base.data.remote.HttpResponse
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : CoroutineBaseViewModel() {
    val isRequestProcessing = MutableLiveData<Boolean>()
    val compositeDisposable = CompositeDisposable()

    fun <T : HttpResponse> executeApi(
        apiCall: () -> Deferred<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((Failure) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) {
        launch(coroutineContext) {
            withContext(Dispatchers.Main) { isRequestProcessing.value = true }
            coroutineScope {
                apiCall().awaitResult().handleResult(onComplete, onFailure, onSuccess)
            }
            withContext(Dispatchers.Main) { isRequestProcessing.value = false }
        }
    }

    fun <T : HttpResponse> executeApiNotProgress(
        apiCall: () -> Deferred<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((Failure) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) {
        launch(coroutineContext) {
            withContext(Dispatchers.Main) {  }
            coroutineScope {
                apiCall().awaitResult().handleResult(onComplete, onFailure, onSuccess)
            }
            withContext(Dispatchers.Main) {  }
        }
    }

    fun executeSuspendCall(repositoryCall: suspend () -> Unit) {
        launch(coroutineContext) {
            withContext(Dispatchers.Main) { isRequestProcessing.value = true }
            repositoryCall()
            withContext(Dispatchers.Main) { isRequestProcessing.value = false }
        }
    }

    fun executeSuspendCallNotProgress(repositoryCall: suspend () -> Unit) {
        launch(coroutineContext) {
            withContext(Dispatchers.Main) { }
            repositoryCall()
            withContext(Dispatchers.Main) { }
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}