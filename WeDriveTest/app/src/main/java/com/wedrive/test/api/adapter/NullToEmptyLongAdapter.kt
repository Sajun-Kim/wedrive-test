package com.wedrive.test.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
internal annotation class NullLong

internal class NullToEmptyLongAdapter {
    @ToJson
    fun toJson(@NullLong value: Long): String? {
        return value.toString()
    }

    @FromJson
    @NullLong
    fun fromJson(value: JsonReader): Long {
        if (value.peek() != JsonReader.Token.NULL) {
            return value.nextLong()
        }
        value.nextNull<Unit>()
        return 0L
    }
}