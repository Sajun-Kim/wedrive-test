package com.namuplanet.base.platfrom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.R
import androidx.core.graphics.drawable.toDrawable

class LoadingProgressDialog(
    context: Context,
    private val allowBackActivityFinish: Boolean = false,
    private val onBackCallback: (Boolean) -> Unit = {}
) :
    Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)

        window?.apply {
            setCancelable(false)
            attributes.width   = WindowManager.LayoutParams.MATCH_PARENT
            attributes.height  = WindowManager.LayoutParams.MATCH_PARENT
            attributes.gravity = Gravity.CENTER
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        setContentView(ProgressBar(context).apply {
            PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN).also {
                indeterminateDrawable.colorFilter = it
            }
        })
    }

    override fun onBackPressed() {
        if (allowBackActivityFinish) {
            dismiss()
            onBackCallback(allowBackActivityFinish)
        } else {
            super.onBackPressed()
        }
    }
}