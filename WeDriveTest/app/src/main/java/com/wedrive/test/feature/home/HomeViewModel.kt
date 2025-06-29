package com.wedrive.test.feature.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.namuplanet.base.event.SingleLiveEvent
import com.namuplanet.base.platfrom.BaseViewModel
import com.namuplanet.base.view.DisplayableItem
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.service.PostService
import com.wedrive.test.extension.getMessage
import com.wedrive.test.feature.home.viewholder.HomeImageItem
import timber.log.Timber

class HomeViewModel : BaseViewModel() {
    private val postService = PostService.authService

    private val items = mutableListOf<DisplayableItem>()

    val showItems = MutableLiveData<List<DisplayableItem>>()
    val moveToHomeDetail = SingleLiveEvent<String>()

    fun initHome(keyword: String = "") {
        items.clear()

        // 전체 게시물 조회
        executeApi(
            apiCall = {
                postService.searchPost(
                    page         = 1,
                    pagePer      = 8,
                    windowWidth  = 1440,
                    windowHeight = 3000,
                    keyword      = keyword
                )
            },
            onSuccess = {
                Timber.d("isMore: ${it.isMore}")

                it.list?.let { list ->
                    Timber.d("size: ${list.size}")
                    Timber.d("result: $list")

                    list.forEach { img ->
                        items.add(
                            HomeImageItem(
                                pid      = img.pid,
                                imageUrl = img.cover,
                                width    = img.cover_size.width,
                                height   = img.cover_size.height,
                                onImageClicked = ::onImageClicked
                            )
                        )
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

    private fun onImageClicked(pid: String) {
        moveToHomeDetail.postValue(pid)
    }
}