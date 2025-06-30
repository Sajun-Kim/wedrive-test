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

class HomeViewModel : BaseViewModel() {
    private val postService = PostService.authService

    private val items = mutableListOf<DisplayableItem>()

    private val appContext    = WeDriveTestApplication.instance.applicationContext
    private val sqliteManager = SQLiteManager(appContext)

    val showItems        = MutableLiveData<List<DisplayableItem>>()
    val addItems         = MutableLiveData<List<DisplayableItem>>()
    val moveToHomeDetail = SingleLiveEvent<Triple<String, Int, Int>>()
    val searchKeyword    = SingleLiveEvent<String>()
    val setIsLoading     = SingleLiveEvent<Boolean>()

    var pageNumber = 2
    var keyword = ""

    fun getCoverImages(keyword: String = "") {
        items.clear()
        pageNumber = 2
        this.keyword = keyword
        setIsLoading.postValue(false)

        val (width, height) = WeDriveTestApplication.instance.getDeviceWidthHeight()
        // 전체 게시물 조회
        executeApi(
            apiCall = {
                postService.searchPost(
                    page         = 1,
                    pagePer      = 10,
                    windowWidth  = width - 20.dpToPx(appContext), // margin 고려
                    windowHeight = height,
                    keyword      = keyword
                )
            },
            onSuccess = {
                it.list?.let { list ->
                    list.forEach { img ->
                        items.add(
                            HomeImageItem(
                                pid            = img.pid,
                                imageUrl       = img.cover,
                                width          = img.cover_size.width,
                                height         = img.cover_size.height,
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

    fun addCoverImages() {
        items.clear()

        val (width, height) = WeDriveTestApplication.instance.getDeviceWidthHeight()
        // 특정 페이지 게시물 조회
        executeApi(
            apiCall = {
                postService.searchPost(
                    page         = pageNumber,
                    pagePer      = 10,
                    windowWidth  = width - 20.dpToPx(appContext), // margin 고려
                    windowHeight = height,
                    keyword      = this.keyword
                )
            },
            onSuccess = {
                it.list?.let { list ->
                    if (list.isEmpty()) {
                        viewModelScope.launchUI {
                            WeDriveTestApplication.instance.showToast("추가 이미지가 없습니다.")
                        }
                        return@executeApi
                    }

                    list.forEach { img ->
                        items.add(
                            HomeImageItem(
                                pid            = img.pid,
                                imageUrl       = img.cover,
                                width          = img.cover_size.width,
                                height         = img.cover_size.height,
                                onImageClicked = ::onImageClicked
                            )
                        )
                    }

                    addItems.postValue(items)
                    pageNumber += 1
                    setIsLoading.postValue(false)
                }
            },
            onFailure = {
                viewModelScope.launchUI {
                    WeDriveTestApplication.instance.showToast(it.getMessage())
                }
            }
        )
    }

    // DB에 저장된 키워드 조회
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

    // DB에 저장된 키워드 모두 제거
    fun deleteAllKeywords() {
        sqliteManager.deleteAllKeywords()
    }

    // 최근 검색 키워드 클릭 시
    fun searchKeyword(keyword: String) {
        searchKeyword.postValue(keyword)
    }

    // DB에 저장된 특정 키워드 제거
    private fun deleteKeyword(keyword: String) {
        sqliteManager.deleteKeyword(keyword)
    }

    // 이미지 클릭 시 상세 화면으로 이동
    private fun onImageClicked(pid: String, width: Int, height: Int) {
        moveToHomeDetail.postValue(Triple(pid, width, height))
    }
}