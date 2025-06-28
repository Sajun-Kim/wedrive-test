package com.namuplanet.base.data.remote

import com.namuplanet.base.data.Failure

interface HttpResponse {
    val apiError: Failure.ApiError?
    fun isSuccessful(): Boolean
    fun onHandled(): Boolean
}