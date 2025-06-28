package com.namuplanet.base.extension

import android.content.res.Resources

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Float.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()