package com.wedrive.test.extension

import com.namuplanet.base.data.Failure
import com.wedrive.test.feature.error.ErrorCode
import timber.log.Timber

infix fun Failure.hasErrorCode(errorCode: String): Boolean =
    when (this) {
        is Failure.ApiError -> this.errorCode == errorCode
        is Failure.HttpError -> this.errorCode == errorCode
        else -> false
    }

fun Failure.containsErrorCode(vararg errorCodes: String): Boolean =
    errorCodes.firstOrNull { hasErrorCode(it) } != null

infix fun Failure.has(errorCodes: Collection<ErrorCode>): Boolean =
    errorCodes.firstOrNull { hasErrorCode(it.errorCode) } != null

infix fun Failure.has(errorCode: ErrorCode): Boolean =
    hasErrorCode(errorCode.errorCode)

infix fun Failure.hasNot(errorCodes: Collection<ErrorCode>): Boolean =
    (this has errorCodes).not()

infix fun Failure.hasNot(errorCode: ErrorCode): Boolean =
    hasErrorCode(errorCode.errorCode).not()

fun Failure.getMessage(): String =
    when (this) {
        is Failure.ApiError -> this.errorMessage
        is Failure.HttpError -> {
            Timber.e("HttpError: ${this.errorMessage}")
            Timber.e("ServerMsg: ${this.serverMessage}")
            this.serverMessage
        }
        is Failure.Exception -> this.throwable.message ?: ""
        else -> ""
    }

fun Failure.getStackTrace(): String = when (this) {
    is Failure.Exception -> this.throwable.stackTraceToString()
    else -> ""
}

fun Throwable.asFailure() = Failure.Exception(this)