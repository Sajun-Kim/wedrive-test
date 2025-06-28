package com.namuplanet.base.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun <T : ViewDataBinding> ViewGroup.bind(layoutId: Int): T {
    return DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, false)
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)

fun View.toggleVisibility() {
    if (isVisible()) invisible() else visible()
}

fun View.toggleVisibilityGone() {
    if (isVisible()) gone() else visible()
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}
