package com.namuplanet.base.platfrom

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

abstract class BaseActivity : AppCompatActivity() {
    private val dlSharedViewModelStore: DlSharedViewModelStore by lazy {
        DlSharedViewModelStore()
    }

    private var progressDialog: LoadingProgressDialog? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dlSharedViewModelStore.clearLbSharedViewModelStore()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.dismiss()
    }

    fun displayProgress(visible: Boolean, allowBackActivityFinish: Boolean = false) {
        when (visible) {
            true  -> showProgress(allowBackActivityFinish)
            false -> hideProgress()
        }
    }

    fun showProgress(allowBackActivityFinish: Boolean = false) {
        when (progressDialog) {
            null -> {
                if (isDestroyed)
                    return

                progressDialog = LoadingProgressDialog(
                    this,
                    allowBackActivityFinish
                ) {
                    if (it)
                        finish()
                }.also { it.show() }
            }
            else -> progressDialog?.let { if (!it.isShowing) it.show() }
        }
    }

    fun hideProgress() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    fun getSharedViewModelStore() = dlSharedViewModelStore

    /**
     * 안드로이드 15 Edge-to-Edge 대응
     * - 상단 상태바만큼 패딩 적용
     * - 하단 내비게이션 바만큼 패딩 적용
     */
    fun setEdgeToEdgeView(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = insets.top)
            v.updatePadding(bottom = insets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }
}