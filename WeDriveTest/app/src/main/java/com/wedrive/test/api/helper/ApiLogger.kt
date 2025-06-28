package com.wedrive.test.api.helper

import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import timber.log.Timber

class ApiLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (message.startsWith("{") or message.startsWith("[")) {
            try {
                Timber.d(JSONObject(message).toString(4))
            } catch (tw: Throwable) {
                Timber.d(message)
            }
        } else {
            Timber.d(message)
        }
    }
}