package com.wedrive.test.vo

data class LoginRequest(
    val mid: String,
    val pwd: String
)

data class AuthResponse(
    val mid     : String,
    val profile : String?,
    val name    : String,
)