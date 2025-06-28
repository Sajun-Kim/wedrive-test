package com.namuplanet.base.data

sealed class Result<T> {
    data class Success<T>(val result: T) : Result<T>()
    data class Error<T>(val failure: Failure) : Result<T>()
    data class HandleResult<T>(val noData: Any? = null) : Result<T>()

    private fun <R> fold(success: (T) -> R, failure: (Failure) -> R) {
        when (this) {
            is Success -> success(this.result)
            is Error -> failure(this.failure)
            else -> Unit
        }
    }

    fun handleResult(
        onComplete: (() -> Unit)? = null,
        onFailure: ((Failure) -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null
    ) {
        fold(
            success = { onSuccess?.invoke(it) },
            failure = { onFailure?.invoke(it) }
        )
        onComplete?.invoke()
    }

    fun getResponse(): T? {
        return if (this is Success) this.result
        else null
    }

    fun getError(): Failure? {
        return if (this is Error) this.failure
        else null
    }
}