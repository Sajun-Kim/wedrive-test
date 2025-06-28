package com.namuplanet.base.extension

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun FragmentActivity.replaceFragment(
    @IdRes frameId: Int,
    fragment: Fragment,
    isAddToBackStackAllowed: Boolean = false
) = supportFragmentManager
    .beginTransaction()
    .apply {
        if (isAddToBackStackAllowed) {
            addToBackStack(null)
        }
    }
    .replace(frameId, fragment)
    .commitAllowingStateLoss()

fun FragmentActivity.addFragment(
    @IdRes frameId: Int,
    hideFragment: Fragment?,
    addFragment: Fragment,
    isAddToBackStackAllowed: Boolean = false
) = supportFragmentManager
    .beginTransaction()
    .apply {
        if (isAddToBackStackAllowed) {
            addToBackStack(null)
        }
        hideFragment?.let {
            hide(it)
        }
    }
    .add(frameId, addFragment)
    .commitAllowingStateLoss()

fun FragmentActivity.addFragment(
    addFragment: Fragment
) = supportFragmentManager
    .beginTransaction()
    .add(android.R.id.content, addFragment)
    .commitAllowingStateLoss()

fun FragmentActivity.initContainer(
    fragment: Fragment,
    @IdRes resId: Int
) = supportFragmentManager.findFragmentById(resId) ?: replaceFragment(
    resId,
    fragment
)

inline fun <reified T : ViewModel> AppCompatActivity.createViewModel(): T {
    return ViewModelProvider(this)[T::class.java]
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> AppCompatActivity.createViewModel(
    crossinline func: () -> T
): T {
    return ViewModelProvider(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>): T = func() as T
    })[T::class.java]
}
