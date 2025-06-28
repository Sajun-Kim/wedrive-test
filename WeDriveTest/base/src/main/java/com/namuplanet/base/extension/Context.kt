package com.namuplanet.base.extension

import android.content.Context
import androidx.core.content.ContextCompat

fun Context.getStringById(resId: Int): String = resources.getString(resId)

fun Context.getStringById(resId: Int, vararg formatArgs: Any): String =
    resources.getString(resId, *formatArgs) ?: ""

fun Context.getColorById(resId: Int) = ContextCompat.getColor(this, resId)