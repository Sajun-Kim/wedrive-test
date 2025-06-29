package com.wedrive.test.utility

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.wedrive.test.extension.dpToPx

object ViewUtil {
    fun changeConstraintMargin(context: Context, layout: ConstraintLayout, view: View, anchor: Int, marginDp: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        val pixel = marginDp.dpToPx(context)

        constraintSet.setMargin(view.id, anchor, pixel)
        constraintSet.applyTo(layout)
    }
}