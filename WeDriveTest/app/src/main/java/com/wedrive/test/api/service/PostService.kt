package com.wedrive.test.api.service

import com.wedrive.test.api.DlRetrofit
import com.wedrive.test.vo.DlPostDetailResponse
import com.wedrive.test.vo.DlPostResponse
import com.wedrive.test.vo.PostResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {
    @GET("post")
    fun searchPost(
        @Query("page")         page         : Int,
        @Query("pagePer")      pagePer      : Int,
        @Query("windowWidth")  windowWidth  : Int,
        @Query("windowHeight") windowHeight : Int,
        @Query("keyword")      keyword      : String,
    ) : DlPostResponse<List<PostResponse>>

    @GET("post/{pid}")
    fun getPostDetail(@Path("pid") pid: String) : DlPostDetailResponse

    companion object {
        val service: PostService = DlRetrofit.createRetrofit()
    }
}