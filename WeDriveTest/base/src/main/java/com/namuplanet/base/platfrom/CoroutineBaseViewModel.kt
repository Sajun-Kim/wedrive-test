package com.namuplanet.base.platfrom

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import com.namuplanet.base.data.Result
import com.namuplanet.base.data.handler.awaitResult
import com.namuplanet.base.data.remote.HttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class CoroutineBaseViewModel : ViewModel(), CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    @CallSuper
    override fun onCleared() {
        job.cancel()
    }

    fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit): Job =
        launch { block() }

    fun CoroutineScope.launchUI(block: suspend CoroutineScope.() -> Unit): Job =
        launch(Dispatchers.Main) { block() }

    suspend fun <T : HttpResponse> execute(deferred: Deferred<T>): Result<T> =
        deferred.awaitResult()
}