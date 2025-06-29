package com.wedrive.test.api

import com.wedrive.test.api.interceptor.DlApiRequestInterceptor
import com.wedrive.test.api.interceptor.DlApiResponseInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpClientProvider {
    fun getHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .apply {
                addInterceptor(DlApiRequestInterceptor())
                addInterceptor(DlApiResponseInterceptor())
                HttpInterceptorProvider.get().forEach { addInterceptor(it) }
            }
            .build()

    fun getHttpClientWithAuth(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .apply {
                authenticator(TokenAuthenticator(getHttpClient()))
                addInterceptor(DlApiRequestInterceptor())
                addInterceptor(DlApiResponseInterceptor())
                HttpInterceptorProvider.get().forEach { addInterceptor(it) }
            }
            .build()
}