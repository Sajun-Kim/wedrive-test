package com.wedrive.test.feature.home

import androidx.core.database.getStringOrNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.namuplanet.base.event.SingleLiveEvent
import com.namuplanet.base.platfrom.BaseViewModel
import com.namuplanet.base.view.DisplayableItem
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.service.PostService
import com.wedrive.test.extension.dpToPx
import com.wedrive.test.extension.getMessage
import com.wedrive.test.feature.home.viewholder.HomeImageItem
import com.wedrive.test.feature.home.viewholder.HomeSearchRecentItem
import com.wedrive.test.utility.sqlite.SQLiteManager
import timber.log.Timber

class HomeViewModel : BaseViewModel() {
    private val postService = PostService.authService

    private val items = mutableListOf<DisplayableItem>()

    private val appContext = WeDriveTestApplication.instance.applicationContext
    private val sqliteManager = SQLiteManager(appContext)

    val showItems = MutableLiveData<List<DisplayableItem>>()
    val moveToHomeDetail = SingleLiveEvent<Triple<String, Int, Int>>()
    val searchKeyword = SingleLiveEvent<String>()

    fun getCoverImages(keyword: String = "") {
        items.clear()

        val (width, height) = WeDriveTestApplication.instance.getDeviceWidthHeight()
        // 전체 게시물 조회
        executeApi(
            apiCall = {
                postService.searchPost(
                    page         = 1,
                    pagePer      = 8,
                    windowWidth  = width - 20.dpToPx(appContext), // margin 고려
                    windowHeight = height,
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

    fun getSavedKeywords(): List<HomeSearchRecentItem> {
        val items = mutableListOf<HomeSearchRecentItem>()
        val cursor = sqliteManager.getAllKeyword()
        if (cursor.moveToFirst()) {
            do {
                val keyword = cursor.getStringOrNull(cursor.getColumnIndex("keyword"))
                if (keyword != null) {
                    items.add(
                        HomeSearchRecentItem(
                            keyword         = keyword,
                            onItemClicked   = ::searchKeyword,
                            onCancelClicked = ::deleteKeyword
                        )
                    )
                }
            } while (cursor.moveToNext())
        }

        return items
    }

    fun deleteAllKeywords() {
        sqliteManager.deleteAllKeywords()
    }

    fun searchKeyword(keyword: String) {
        searchKeyword.postValue(keyword)
    }

    private fun deleteKeyword(keyword: String) {
        sqliteManager.deleteKeyword(keyword)
    }

    private fun onImageClicked(pid: String, width: Int, height: Int) {
        moveToHomeDetail.postValue(Triple(pid, width, height))
    }
}