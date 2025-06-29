package com.namuplanet.base.data

sealed class Failure {
    data class ApiError(val errorCode: String, val errorMessage: String) : Failure()
    data class HttpError(val errorCode: String, val errorMessage: String, val serverMessage: String) : Failure()
    data class Exception(val throwable: Throwable) : Failure()
    data class AuthError(val errorCode: String, val errorMessage: String, val serverMessage: String) : Failure()
    data object TimeOutError : Failure()
    data object NoConnection : Failure()
}