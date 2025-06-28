package com.wedrive.test.api.interceptor

import com.wedrive.test.WeDriveTestApplication
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.Locale

class DlApiRequestInterceptor: Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.build {
            if (!originalRequest.containsHeader(DlApiHeader.CONTENT_TYPE))
                header(DlApiHeader.CONTENT_TYPE, "application/json")

            if (!originalRequest.containsHeader(DlApiHeader.AUTH_TOKEN) &&
                WeDriveTestApplication.instance.pref.get<String>(DlApiHeader.AUTH_TOKEN).isNotEmpty())
                header(DlApiHeader.AUTH_TOKEN, WeDriveTestApplication.instance.pref.get<String>(DlApiHeader.AUTH_TOKEN))

            if (!originalRequest.containsHeader(DlApiHeader.LOCALE_CODE))
                header(DlApiHeader.LOCALE_CODE, Locale.getDefault().toLanguageTag())

            header(DlApiHeader.DEVICE_NM    , android.os.Build.MODEL)                              // 기기 모델명
            header(DlApiHeader.OS_TYPE      , DlApiHeader.OS_ANDROID)                              // OS 구분
            header(DlApiHeader.APP_VERSION  , WeDriveTestApplication.instance.getAppVersionName()) // App 버전 : x.x.x
            header(DlApiHeader.OS_VERSION   , android.os.Build.VERSION.SDK_INT.toString())         // OS 버전  : API Level(26, 27, ...)
        }
        Timber.tag("ApiLogger").d("===== API Request Header =====\n${newRequest.headers}==============================")
        return chain.proceed(newRequest)
    }

    private fun Request.containsHeader(name: String) = header(name) != null
    private fun Request.build(builder: Request.Builder.() -> Unit): Request =
        newBuilder().apply(builder).build()
}

object DlApiHeader {
    const val CONTENT_TYPE = "Content-Type"
    const val LOCALE_CODE  = "lclCd"
    const val OS_TYPE      = "osKind"
    const val OS_ANDROID   = "aos"
    const val APP_VERSION  = "version"
    const val AUTH_TOKEN   = "Authorization"
    const val DEVICE_NM    = "deviceNm"
    const val OS_VERSION   = "osVersion"
}