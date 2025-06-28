package com.namuplanet.base.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.observe(owner: LifecycleOwner?, observe: (T) -> Unit) =
    owner?.let { lifeCycleOwner ->
        observe(
            lifeCycleOwner, Observer { data ->
                data?.let { observe(data) }
            }
        )
    }

fun <T> LiveData<T>.observeNullable(owner: LifecycleOwner?, observe: (T?) -> Unit) =
    owner?.let { lifeCycleOwner ->
        observe(
            lifeCycleOwner, Observer { observe(it) }
        )
    }

fun <T> LiveData<T>.observeIfChanged(owner: LifecycleOwner?, observe: (T) -> Unit) =
    owner?.let { lifeCycleOwner ->
        var previousValue: T? = null

        observe(
            lifeCycleOwner, Observer { data ->
                data?.let {
                    if (previousValue == data) return@Observer
                    previousValue = data

                    observe(data)
                }
            }
        )
    }

fun <T> LiveData<T>.blockingObserve(block: (T) -> Unit) {
    val result = blockingObserve()
    result?.let {
        block(result)
    } ?: throw IllegalStateException()
}

fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val observer = Observer<T> { t ->
        value = t
        latch.countDown()
    }
    observeForever(observer)
    latch.await(7, TimeUnit.SECONDS)

    return value
}

fun <T, R> LiveData<T>.mapNotNull(mapper: (T) -> R): LiveData<R> =
    MediatorLiveData<R>().also { result ->
        result.addSource(this) { if (it != null) result.value = mapper(it) }
    }

fun <T, R> LiveData<T>.mapMutable(mapper: (T?) -> R): MutableLiveData<R> =
    MediatorLiveData<R>().also { result ->
        result.addSource(this) { result.value = mapper(it) }
    }

fun LiveData<*>.hasValue() = value != null
fun MutableLiveData<Boolean>.setOnlyTrue(u: () -> Boolean) {
    if (value == true) value = u.invoke()
}

fun <T, R> LiveData<T>.mapMutableNotNull(mapper: (T) -> R): MutableLiveData<R> =
    MediatorLiveData<R>().also { result ->
        result.addSource(this) { if (it != null) result.value = mapper(it) }
    }

fun <T> LiveData<T>.filter(predicate: (T) -> Boolean): LiveData<T> = MediatorLiveData<T>().apply {
    addSource(this@filter) {
        if (predicate(it)) {
            value = it
        }
    }
}
