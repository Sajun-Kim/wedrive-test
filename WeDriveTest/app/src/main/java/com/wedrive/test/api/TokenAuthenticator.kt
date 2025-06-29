package com.wedrive.test.api

import com.wedrive.test.R
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.DlRetrofit.moshi
import com.wedrive.test.api.interceptor.DlApiHeader
import com.wedrive.test.api.service.TokenService
import com.wedrive.test.utility.getString
import com.wedrive.test.vo.RefreshRequest
import com.wedrive.test.vo.TokenResponse
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

        if (currentAccessTokenInHeader != storedAccessToken && storedAccessToken.isNotBlank()) {
            return response.request.newBuilder()
                .header(DlApiHeader.AUTH_TOKEN, storedAccessToken)
                .build()
        }

        // 여러 스레드가 동시에 토큰 갱신을 시도하는 것을 방지
        synchronized(this) {
            // synchronized 블록에 진입한 후, 다른 스레드가 이미 토큰을 갱신했는지 다시 확인
            val latestStoredAccessToken = pref.get<String>(DlApiHeader.AUTH_TOKEN)
            if (currentAccessTokenInHeader != latestStoredAccessToken && latestStoredAccessToken.isNotBlank()) {
                // 다른 스레드가 토큰을 갱신했으므로, 그 토큰으로 재시도
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
            } else { // 리프레시 실패 시
                pref.clearAll() // 토큰 삭제
                null            // 요청 중단
            }
        }
    }

    private fun refreshAccessTokenSync(): String? {
        val currentRefreshToken = pref.get<String>(DlApiHeader.REFRESH_TOKEN)

        // 리프레시 토큰 없으면 갱신 불가능
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
                    // 여기서도 로그아웃 처리가 필요할 수 있음
                    pref.clearAll()
                }
                return null
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during token refresh.")
            return null
        }
    }
}