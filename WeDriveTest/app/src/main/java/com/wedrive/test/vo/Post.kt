package com.wedrive.test.vo

data class PostResponse(
    val pid        : String,
    val cover      : String,
    val cover_size : PostCoverSize
)

data class PostCoverSize(
    val width  : Int,
    val height : Int
)