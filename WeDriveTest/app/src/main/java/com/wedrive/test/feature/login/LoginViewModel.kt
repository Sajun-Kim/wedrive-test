package com.wedrive.test.feature.login

import androidx.lifecycle.viewModelScope
import com.namuplanet.base.event.SingleLiveEvent
import com.namuplanet.base.platfrom.BaseViewModel
import com.wedrive.test.vo.LoginRequest
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.interceptor.DlApiHeader
import com.wedrive.test.api.service.AuthService
import com.wedrive.test.extension.getMessage
import timber.log.Timber

class LoginViewModel : BaseViewModel() {

    private val authService = AuthService.authService

    val moveToHome = SingleLiveEvent<Void?>()

    fun login(mid: String, pwd: String) {
        executeApi(
            apiCall = {
                authService.getAuthToken(LoginRequest(mid, pwd))
            },
            onSuccess = {
                Timber.d("access token  : ${it.accessToken}")
                Timber.d("refresh token : ${it.refreshToken}")

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