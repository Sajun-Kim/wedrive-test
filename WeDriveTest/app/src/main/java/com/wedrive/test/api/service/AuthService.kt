package com.wedrive.test.api.service

import com.wedrive.test.vo.LoginRequest
import com.wedrive.test.api.DlRetrofit
import com.wedrive.test.vo.UserInfoResponse
import com.wedrive.test.vo.DlUserInfoResponse
import com.wedrive.test.vo.DlLoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    // 포인트 전체 조회
    @POST("auth/login")
    fun getAuthToken(@Body param: LoginRequest): DlLoginResponse

    // 사용자 정보 조회
    @GET("auth")
    fun getUserInfo(): DlUserInfoResponse<UserInfoResponse>

    companion object {
        val service: AuthService = DlRetrofit.createRetrofit()
    }
}