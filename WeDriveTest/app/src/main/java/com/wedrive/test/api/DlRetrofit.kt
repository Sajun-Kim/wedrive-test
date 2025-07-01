package com.wedrive.test.api

import com.wedrive.test.api.adapter.JsonAdapterProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.wedrive.test.R
import com.wedrive.test.utility.getString
import okhttp3.OkHttpClient

object DlRetrofit {
    private val httpClientAuth: OkHttpClient = HttpClientProvider.getHttpClientWithAuth()

    val moshi: Moshi = Moshi.Builder()
        .apply {
            add(KotlinJsonAdapterFactory())
            JsonAdapterProvider.get().forEach { add(it) }
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder().baseUrl(getString(R.string.api_host))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(httpClientAuth)
        .build()

    inline fun <reified T> createRetrofit(): T = retrofit.create(T::class.java)
}