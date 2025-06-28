package com.namuplanet.base.event

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class LiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)
    private val observers = HashMap<Int, Observer<in T>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        Observer<T> { t ->
            if (pending.compareAndSet(true, false)) {
                observers.values.forEach { observer ->
                    observer.onChanged(t)
                }
            }
        }.let {
            observers[it.hashCode()] = observer
            super.observe(owner, it)
        }
    }

    @MainThread
    override fun removeObservers(owner: LifecycleOwner) {
        observers.clear()
        super.removeObservers(owner)
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        observers.remove(observer.hashCode())
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    @MainThread
    fun call() {
        value = null
    }
}