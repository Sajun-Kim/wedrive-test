package com.namuplanet.base.view

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T, L> : RecyclerView.ViewHolder, LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    constructor(binding: ViewDataBinding) : super(binding.root) {
        @Suppress("LeakingThis")
        binding.lifecycleOwner = this
    }

    constructor(view: View) : super(view)

    //    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    override val lifecycle: Lifecycle = lifecycleRegistry

    abstract fun bind(item: T, itemListener: L?)

    fun onViewAttachedToWindow() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }
}

abstract class BasePayloadsViewHolder<T, L> :
    BaseViewHolder<T, L> {
    constructor(binding: ViewDataBinding) : super(binding.root)
    constructor(view: View) : super(view)

    abstract fun bind(item: T, itemListener: L?, payloads: MutableList<Any>?)
}

abstract class BaseDynamicViewHolder<T, L> :
    BaseViewHolder<T, L> {
    constructor(binding: ViewDataBinding) : super(binding.root)
    constructor(view: View) : super(view)

    abstract fun bind(item: T, itemListener: L?, dynamicData: BaseDynamicData?)
}
