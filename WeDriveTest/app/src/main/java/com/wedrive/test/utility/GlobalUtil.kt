package com.wedrive.test.utility

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.wedrive.test.WeDriveTestApplication

fun getString(@StringRes resId: Int): String = WeDriveTestApplication.instance.getString(resId)

fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
    WeDriveTestApplication.instance.getString(resId, formatArgs)

@ColorInt
fun getColor(@ColorRes resId: Int): Int = ContextCompat.getColor(WeDriveTestApplication.instance, resId)