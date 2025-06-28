package com.wedrive.test.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
internal annotation class NullString

internal class NullToEmptyAdapter {
    @ToJson
    fun toJson(@NullString value: String): String? {
        return value
    }

    @FromJson
    @NullString
    fun fromJson(value: JsonReader): String {
        if (value.peek() != JsonReader.Token.NULL) {
            return value.nextString()
        }
        value.nextNull<Unit>()
        return ""
    }
}