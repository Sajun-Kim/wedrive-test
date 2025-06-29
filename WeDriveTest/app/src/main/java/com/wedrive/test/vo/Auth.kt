package com.wedrive.test.vo

data class LoginRequest(
    val mid: String,
    val pwd: String
)

data class UserInfoResponse(
    val mid     : String,
    val profile : String?,
    val name    : String,
)

data class RefreshRequest(
    val refreshToken: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)