package com.namuplanet.base.extension

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener

fun FragmentManager.onBackStackChanged(
    block: OnBackStackChangedListener.(FragmentManager) -> Unit
): OnBackStackChangedListener {
    lateinit var listener: OnBackStackChangedListener
    return OnBackStackChangedListener {
        block(listener, this)
    }.also {
        listener = it
        addOnBackStackChangedListener(it)
    }
}