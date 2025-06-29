package com.wedrive.test.feature.login

import androidx.lifecycle.viewModelScope
import com.namuplanet.base.event.SingleLiveEvent
import com.namuplanet.base.platfrom.BaseViewModel
import com.wedrive.test.vo.LoginRequest
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.interceptor.DlApiHeader
import com.wedrive.test.api.service.LoginService
import com.wedrive.test.extension.getMessage
import timber.log.Timber

class LoginViewModel : BaseViewModel() {
    private val loginService = LoginService.service

    val moveToHome = SingleLiveEvent<Void?>()

    fun login(mid: String, pwd: String) {
        executeApi(
            apiCall = {
//                loginService.getAuthToken(LoginRequest(mid, pwd))
                loginService.getAuthToken(LoginRequest("test001", "74726556"))
            },
            onSuccess = {
                Timber.d("access token  : ${it.accessToken}")
                Timber.d("refresh token : ${it.refreshToken}")

                WeDriveTestApplication.instance.pref[DlApiHeader.AUTH_TOKEN]    = "Bearer ${it.accessToken}"
                WeDriveTestApplication.instance.pref[DlApiHeader.REFRESH_TOKEN] = "Bearer ${it.refreshToken}"

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