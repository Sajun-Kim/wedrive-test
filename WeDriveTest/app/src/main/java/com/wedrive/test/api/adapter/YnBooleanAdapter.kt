package com.wedrive.test.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson

private const val YES = "Y"
private const val NO = "N"

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
internal annotation class YnBoolean

internal class YnBooleanAdapter {
    @ToJson
    fun toJsonNotNull(@YnBoolean value: Boolean) = when (value) {
        true -> YES
        else -> NO
    }

    @FromJson
    @YnBoolean
    fun fromJsonNotNull(value: String) = when (value) {
        YES -> true
        else -> false
    }

    @ToJson
    fun toJsonNullable(@YnBoolean value: Boolean?) = when (value) {
        true -> YES
        false -> NO
        else -> null
    }

    @FromJson
    @YnBoolean
    fun fromJsonNullable(value: String?) = when (value) {
        YES -> true
        NO -> false
        else -> null
    }
}