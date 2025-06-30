package com.namuplanet.base.view

import android.view.ViewGroup

abstract class ViewHolderProvider(val itemListener: ItemListener? = null) {
    abstract fun layoutId(): Int
    abstract fun create(parent: ViewGroup): BaseViewHolder<*, *>
}