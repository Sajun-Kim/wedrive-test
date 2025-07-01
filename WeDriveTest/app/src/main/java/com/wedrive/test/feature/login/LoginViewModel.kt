package com.wedrive.test.feature.login

import androidx.lifecycle.viewModelScope
import com.namuplanet.base.event.SingleLiveEvent
import com.namuplanet.base.platfrom.BaseViewModel
import com.wedrive.test.vo.LoginRequest
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.interceptor.DlApiHeader
import com.wedrive.test.api.service.AuthService
import com.wedrive.test.extension.getMessage

class LoginViewModel : BaseViewModel() {
    private val authService = AuthService.service

    val moveToHome = SingleLiveEvent<Void?>()

    fun login(mid: String, pwd: String) {
        executeApi(
            apiCall   = { authService.getAuthToken(LoginRequest(mid, pwd)) },
            onSuccess = {
                // Preference에 토큰 저장
                WeDriveTestApplication.instance.pref[DlApiHeader.AUTH_TOKEN]    = "Bearer ${it.accessToken}"
                WeDriveTestApplication.instance.pref[DlApiHeader.REFRESH_TOKEN] = "${it.refreshToken}"

                moveToHome.postCall()
            },
            onFailure = {
                viewModelScope.launchUI {
                    WeDriveTestApplication.instance.showToast(it.getMessage())
                }
            }
        )
    }
}