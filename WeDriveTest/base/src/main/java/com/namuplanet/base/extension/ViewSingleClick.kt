package com.namuplanet.base.extension

import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

fun View.setOnSingleClickListener(click: (v: View) -> Unit) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View) {
            click(v)
        }
    })
}

abstract class OnSingleClickListener(waitTime: Long = WAIT_TIME_NORMAL) :
    View.OnClickListener {
    companion object {
        const val WAIT_TIME_NORMAL = 300L
        const val WAIT_TIME_LONG = 500L
    }

    private val viewPublishSubject = PublishSubject.create<View>()

    init {
        viewPublishSubject
            .throttleFirst(waitTime, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    // Timber.e("single click error message: ${it.message}")
                },
                onNext = {
                    onSingleClick(it)
                }
            )
    }

    override fun onClick(v: View) {
        // Timber.d("onClick viewId:${v.id}")
        viewPublishSubject.onNext(v)
    }

    abstract fun onSingleClick(v: View)
}