package com.wedrive.test.api.interceptor

import com.wedrive.test.WeDriveTestApplication
import okhttp3.Interceptor
import okhttp3.Response

class DlApiResponseInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request()).also { res ->
            if (res.isSuccessful) {
                if (res.headers(DlApiHeader.AUTH_TOKEN).isNotEmpty()) {
                    val token = "Bearer " + res.header(DlApiHeader.AUTH_TOKEN)
                    WeDriveTestApplication.instance.pref[DlApiHeader.AUTH_TOKEN] = token
                }
            }
        }
    }
}