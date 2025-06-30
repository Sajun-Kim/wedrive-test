package com.wedrive.test.feature.home

import androidx.core.database.getStringOrNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.namuplanet.base.event.SingleLiveEvent
import com.namuplanet.base.platfrom.BaseViewModel
import com.namuplanet.base.view.DisplayableItem
import com.wedrive.test.R
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.api.service.PostService
import com.wedrive.test.extension.dpToPx
import com.wedrive.test.extension.getMessage
import com.wedrive.test.feature.home.viewholder.HomeImageItem
import com.wedrive.test.feature.home.viewholder.HomeSearchRecentItem
import com.wedrive.test.utility.getString
import com.wedrive.test.utility.sqlite.SQLiteManager
import com.wedrive.test.vo.PostResponse

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

    private var pageNumber = 2
    private var keyword = ""

    fun getPosts(keyword: String = "") {
        items.clear()
        pageNumber = 2
        this.keyword = keyword
        setIsLoading.postValue(false)

        // 최초 게시물 조회
        callPostApi { list ->
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
    }

    fun addPosts() {
        items.clear()

        // 특정 페이지 게시물 조회
        callPostApi(pageNumber) { list ->
            if (list.isEmpty()) {
                viewModelScope.launchUI {
                    WeDriveTestApplication.instance.showToast(getString(R.string.home_last_post))
                }
                return@callPostApi
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
    }

    private fun callPostApi(page: Int = 1, callback: (List<PostResponse>) -> Unit) {
        val (width, height) = WeDriveTestApplication.instance.getDeviceWidthHeight()

        executeApi(
            apiCall = {
                postService.searchPost(
                    page         = page,
                    pagePer      = 10,
                    windowWidth  = width - 20.dpToPx(appContext), // margin 고려
                    windowHeight = height,
                    keyword      = keyword
                )
            },
            onSuccess = {
                it.list?.let { list ->
                    callback(list)
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
        sqliteManager.insertOrUpdateKeyword(keyword)
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