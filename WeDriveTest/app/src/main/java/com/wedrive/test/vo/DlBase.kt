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

open class DlUserInfoBase<T>(
    val msg: String? = null,
    val user: T? = null
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

open class DlPostBase<T>(
    val msg: String? = null,
    val list: T? = null,
    val isMore: Boolean = false
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

open class DlPostDetailBase(
    val msg : String? = null,
    val pid: String,
    val cover: String,
    val ratio: Float,
    val title: String,
    val context: String
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