package com.wedrive.test.api

import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import com.wedrive.test.api.helper.ApiLogger

object HttpInterceptorProvider {
    fun get(): List<Interceptor> {
        return listOf(HttpLoggingInterceptor(ApiLogger()).apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }
}