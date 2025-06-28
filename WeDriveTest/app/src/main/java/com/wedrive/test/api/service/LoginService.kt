package com.wedrive.test.api.service

import com.wedrive.test.vo.LoginRequest
import com.wedrive.test.api.DlRetrofit
import com.wedrive.test.vo.DlLoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    // 포인트 전체 조회
    @POST("auth/login")
    fun getAuthToken(@Body param: LoginRequest): DlLoginResponse

    companion object {
        val service: LoginService = DlRetrofit.createRetrofit()
    }
}