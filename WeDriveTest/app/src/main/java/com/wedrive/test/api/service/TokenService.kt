package com.wedrive.test.api.service

import com.wedrive.test.vo.RefreshRequest
import com.wedrive.test.vo.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {
    // 토큰 재발급
    @POST("auth/refresh")
    fun getNewToken(@Body param: RefreshRequest): Call<TokenResponse>
}