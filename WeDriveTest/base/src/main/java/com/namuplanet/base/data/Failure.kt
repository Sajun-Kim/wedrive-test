package com.namuplanet.base.data

sealed class Failure {
    data class ApiError(val errorCode: String, val errorMessage: String) : Failure()
    data class HttpError(val errorCode: String, val errorMessage: String) : Failure()
    data class Exception(val throwable: Throwable) : Failure()
    data object AuthError : Failure()
    data object TimeOutError : Failure()
    data object NoConnection : Failure()
}