package com.wedrive.test.api

import com.wedrive.test.api.interceptor.DlApiRequestInterceptor
import com.wedrive.test.api.interceptor.DlApiResponseInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpClientProvider {
    // Authenticator 없는 Client
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

    // Authenticator 있는 Client
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