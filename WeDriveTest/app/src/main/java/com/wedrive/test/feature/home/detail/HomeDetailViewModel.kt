package com.wedrive.test.feature.home.detail

import androidx.lifecycle.MutableLiveData
import com.namuplanet.base.platfrom.BaseViewModel
import com.wedrive.test.api.service.AuthService
import com.wedrive.test.api.service.PostService
import com.wedrive.test.extension.getMessage
import timber.log.Timber

class HomeDetailViewModel : BaseViewModel() {
    private val authService = AuthService.service
    private val postService = PostService.service

    val postDetailItem = PostDetailItem()

    fun initHomeDetail(pid: String) {
        // 게시물 상세 조회
        executeApi(
            apiCall   = { postService.getPostDetail(pid) },
            onSuccess = {
                postDetailItem.cover.postValue(it.cover)
                postDetailItem.title.postValue(it.title)
                postDetailItem.context.postValue(it.context)
            },
            onFailure = {
                Timber.e("getPostDetail error\n${it.getMessage()}")
            }
        )

        // 사용자 정보 조회
        executeApi(
            apiCall   = { authService.getUserInfo() },
            onSuccess = {
                it.user?.let { user ->
                    postDetailItem.profile.postValue(user.profile)
                    postDetailItem.name.postValue(user.name)
                }
            },
            onFailure = {
                Timber.e("userinfo error\n${it.getMessage()}")
            }
        )
    }
}

data class PostDetailItem(
    // 사용자 정보
    val profile : MutableLiveData<String> = MutableLiveData(""),
    val name    : MutableLiveData<String> = MutableLiveData(""),

    // 게시물 정보
    val pid     : MutableLiveData<String> = MutableLiveData(""),
    val cover   : MutableLiveData<String> = MutableLiveData(""),
    val ratio   : MutableLiveData<String> = MutableLiveData(""),
    val title   : MutableLiveData<String> = MutableLiveData(""),
    val context : MutableLiveData<String> = MutableLiveData("")
)