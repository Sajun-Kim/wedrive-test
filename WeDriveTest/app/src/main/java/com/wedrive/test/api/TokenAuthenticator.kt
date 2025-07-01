package com.wedrive.test.api

import com.wedrive.test.R
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.DlRetrofit.moshi
import com.wedrive.test.api.interceptor.DlApiHeader
import com.wedrive.test.api.service.TokenService
import com.wedrive.test.utility.getString
import com.wedrive.test.vo.RefreshRequest
import com.wedrive.test.vo.TokenResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import kotlin.text.isNotBlank

class TokenAuthenticator(
    private val tokenRefreshClient: OkHttpClient
) : Authenticator {
    private val pref = WeDriveTestApplication.instance.pref

    private val tokenService: TokenService by lazy {
        Retrofit.Builder()
            .baseUrl(getString(R.string.api_host))
            .client(tokenRefreshClient) // Authenticator 없는 클라이언트 사용
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TokenService::class.java)
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val currentAccessTokenInHeader = response.request.header(DlApiHeader.AUTH_TOKEN)
        val storedAccessToken = pref.get<String>(DlApiHeader.AUTH_TOKEN)
        Timber.d("currentToken : $currentAccessTokenInHeader")
        Timber.d("storedToken  : $storedAccessToken")
        Timber.d("originUrl    : Bearer $${response.request.url}")

        // 현재 Access Token과 Preference에 저장된 Access Token 비교 후 다르면
        // 저장된 토큰이 다른 작업에 의해 최신화된 토큰으로 판단해 해당 토큰으로 요청 재시도
        if (currentAccessTokenInHeader != storedAccessToken && storedAccessToken.isNotBlank()) {
            return response.request.newBuilder()
                .header(DlApiHeader.AUTH_TOKEN, storedAccessToken)
                .build()
        }

        // 여러 스레드가 동시에 토큰 갱신을 시도하는 것을 방지
        synchronized(this) {
            // synchronized 블록에 진입한 후, 다른 스레드가 이미 토큰을 갱신했는지 다시 확인
            val latestStoredAccessToken = pref.get<String>(DlApiHeader.AUTH_TOKEN)
            // 위와 동일한 작업(현재 토큰, 저장 토큰 비교)
            if (currentAccessTokenInHeader != latestStoredAccessToken && latestStoredAccessToken.isNotBlank()) {
                return response.request.newBuilder()
                    .header(DlApiHeader.AUTH_TOKEN, latestStoredAccessToken)
                    .build()
            }

            // 토큰 갱신 로직 실행
            val newAccessToken = refreshAccessTokenSync()
            Timber.d("newToken     : Bearer $newAccessToken")
            Timber.d("url          : Bearer $${response.request.url}")
            Timber.d("=====================================================")

            return if (newAccessToken != null) {
                // 새 액세스 토큰으로 이전 요청 재시도
                pref[DlApiHeader.AUTH_TOKEN] = "Bearer $newAccessToken" // 새 토큰 저장

                // auth/fail로 리다이렉트 된 경우 처리(무한 재시도 방지)
                // 기본적으로 이전 요청(토큰 refresh 전 실패한 요청)을 다시 실행하는데,
                // auth/fail로 리다이렉트 되면 토큰 refresh를 계속 시도하므로 url 직접 지정
                if (response.request.url.toString().endsWith("auth/fail")) {
                    response.request.newBuilder()
                        .url("https://codetest.wedrive.kr:7880/auth")
                        .header(DlApiHeader.AUTH_TOKEN, "Bearer $newAccessToken")
                        .build()
                }
                else {
                    response.request.newBuilder()
                        .header(DlApiHeader.AUTH_TOKEN, "Bearer $newAccessToken")
                        .build()
                }
            }
            else { // 리프레시 실패 시
                // 토큰 삭제 및 앱 재시작
                pref.clearAll()
                CoroutineScope(Dispatchers.Main).launch {
                    WeDriveTestApplication.instance.showToast(getString(R.string.login_auth_fail))
                }
                WeDriveTestApplication.instance.restartApp()

                null // 요청 중단
            }
        }
    }

    private fun refreshAccessTokenSync(): String? {
        val currentRefreshToken = pref.get<String>(DlApiHeader.REFRESH_TOKEN)

        // 리프레시 토큰 없으면 갱신 불가
        if (currentRefreshToken.isBlank())
            return null

        try {
            val call: Call<TokenResponse> = tokenService.getNewToken(RefreshRequest(currentRefreshToken))
            val response: retrofit2.Response<TokenResponse> = call.execute() // 동기식 호출

            if (response.isSuccessful) {
                val newTokenData = response.body()
                if (newTokenData != null) {
                    // 새로 받은 리프레시 토큰 저장
                    pref[DlApiHeader.REFRESH_TOKEN] = newTokenData.refreshToken
                    return newTokenData.accessToken
                } else {
                    // reponse body가 null인 경우
                    Timber.e("Refresh token response body is null.")
                    return null
                }
            } else {
                // API 호출 실패
                 Timber.e("Failed to refresh token. Code: ${response.code()}, Message: ${response.message()}")
                if (response.code() == 401 || response.code() == 403) {
                    // 리프레시 토큰이 만료되었거나 유효하지 않음

                    // 토큰 삭제 및 앱 재시작
                    pref.clearAll()
                    CoroutineScope(Dispatchers.Main).launch {
                        WeDriveTestApplication.instance.showToast(getString(R.string.login_auth_fail))
                    }
                    WeDriveTestApplication.instance.restartApp()
                }
                return null
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during token refresh.")
            return null
        }
    }
}