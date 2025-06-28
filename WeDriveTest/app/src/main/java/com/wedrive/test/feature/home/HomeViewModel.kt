package com.wedrive.test.feature.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.namuplanet.base.platfrom.BaseViewModel
import com.namuplanet.base.view.DisplayableItem
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.service.PostService
import com.wedrive.test.extension.getMessage
import com.wedrive.test.feature.home.viewholder.HomeImageItem
import timber.log.Timber

class HomeViewModel : BaseViewModel() {
    private val postService = PostService.service

    private val items = mutableListOf<DisplayableItem>()

    val showItems = MutableLiveData<List<DisplayableItem>>()

    fun initHome() {
        // 전체 게시물 조회
        items.clear()
        executeApi(
            apiCall = {
                postService.getPost(
                    page         = 1,
                    pagePer      = 8,
                    windowWidth  = 390,
                    windowHeight = 500
                )
            },
            onSuccess = {
                Timber.d("isMore: ${it.isMore}")
                it.list?.let { list ->
                    Timber.d("size: ${list.size}")
                    Timber.d("result: $list")

                    list.forEach { img ->
                        items.add(HomeImageItem(img.cover))
                    }

                    showItems.postValue(items)
                }
            },
            onFailure = {
                viewModelScope.launchUI {
                    WeDriveTestApplication.instance.showToast(it.getMessage())
                }
            }
        )
    }
}