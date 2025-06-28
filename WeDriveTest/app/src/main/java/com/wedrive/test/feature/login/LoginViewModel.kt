package com.wedrive.test.feature.login

import androidx.lifecycle.viewModelScope
import com.namuplanet.base.platfrom.BaseViewModel
import com.wedrive.test.vo.LoginRequest
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.interceptor.DlApiHeader
import com.wedrive.test.api.service.LoginService
import com.wedrive.test.extension.getMessage
import timber.log.Timber

class LoginViewModel : BaseViewModel() {
    private val loginService = LoginService.service

    fun login(mid: String, pwd: String) {
        executeApi(
            apiCall = {
                loginService.getAuthToken(LoginRequest(mid, pwd))
            },
            onSuccess = {
                Timber.d("access token  : ${it.accessToken}")
                Timber.d("refresh token : ${it.refreshToken}")

                WeDriveTestApplication.instance.pref[DlApiHeader.AUTH_TOKEN] = it.accessToken
            },
            onFailure = {
                viewModelScope.launchUI {
                    WeDriveTestApplication.instance.showToast(it.getMessage())
                }
            }
        )
    }
}