package com.wedrive.test.vo

import com.namuplanet.base.data.Failure
import com.namuplanet.base.data.remote.HttpResponse

open class DlBase<DATA : Any?>(
    val msg: String? = null,
    val code: String = "",
    val message: String? = null,
    val contents: DATA
) : HttpResponse {
    override fun isSuccessful(): Boolean {
        return msg.isNullOrEmpty()
    }

    override val apiError: Failure.ApiError?
        get() {
            if (isSuccessful()) return null
            return Failure.ApiError(code, message ?: "")
        }

    override fun onHandled(): Boolean {
        return false
    }
}

open class DlBaseEmpty(
    val msg: String? = null,
    val code: String = "",
    val message: String? = null
) : HttpResponse {
    override fun isSuccessful(): Boolean {
        return msg.isNullOrEmpty()
    }

    override val apiError: Failure.ApiError?
        get() {
            if (isSuccessful()) return null
            return Failure.ApiError(code, message ?: "")
        }

    override fun onHandled(): Boolean {
        return false
    }
}

open class DlNullableBase<T>(
    val msg: String? = null,
    val code: String = "",
    val message: String? = null,
    val contents: T? = null
) : HttpResponse {
    override fun isSuccessful(): Boolean {
        return msg.isNullOrEmpty()
    }

    override val apiError: Failure.ApiError?
        get() {
            if (isSuccessful()) return null
            return Failure.ApiError(code, message ?: "")
        }

    override fun onHandled(): Boolean {
        return false
    }
}

open class DlLoginBase(
    val msg: String? = null,
    val accessToken: String? = "",
    val refreshToken: String? = ""
) : HttpResponse {
    override fun isSuccessful(): Boolean {
        return msg.isNullOrEmpty()
    }

    override val apiError: Failure.ApiError?
        get() {
            if (isSuccessful()) return null
            return Failure.ApiError("00001", msg ?: "")
        }

    override fun onHandled(): Boolean {
        return false
    }
}