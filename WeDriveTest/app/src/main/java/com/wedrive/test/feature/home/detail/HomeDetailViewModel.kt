package com.wedrive.test.feature.home.detail

import com.namuplanet.base.platfrom.BaseViewModel
import com.wedrive.test.api.service.PostService
import timber.log.Timber

class HomeDetailViewModel : BaseViewModel() {
    private val postService = PostService.service

    fun initHomeDetail(pid: String) {
        executeApi(
            apiCall   = { postService.getPostDetail(pid) },
            onSuccess = {
                Timber.d("pid     : ${it.pid}")
                Timber.d("cover   : ${it.cover}")
                Timber.d("ratio   : ${it.ratio}")
                Timber.d("title   : ${it.title}")
                Timber.d("context : ${it.context}")
            },
            onFailure = {
                Timber.e("failure")
            }
        )
    }
}