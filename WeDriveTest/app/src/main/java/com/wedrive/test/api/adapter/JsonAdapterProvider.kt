package com.wedrive.test.api.adapter

object JsonAdapterProvider {
    fun get() = listOf(
        NullToEmptyAdapter(),
        NullToEmptyLongAdapter(),
        YnBooleanAdapter()
    )
}