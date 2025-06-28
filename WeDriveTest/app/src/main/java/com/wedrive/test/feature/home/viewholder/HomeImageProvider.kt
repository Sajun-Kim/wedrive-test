package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import com.namuplanet.base.extension.bind
import com.namuplanet.base.view.BaseViewHolder
import com.namuplanet.base.view.DisplayableItem
import com.namuplanet.base.view.ViewHolderProvider
import com.wedrive.test.R
import com.wedrive.test.databinding.ItemHomeImageBinding

private val LAYOUT_ID = R.layout.item_home_image

data class HomeImageItem(
    val imageUrl: String
): DisplayableItem(LAYOUT_ID)

class HomeImageViewHolder(val binding: ItemHomeImageBinding):
    BaseViewHolder<HomeImageItem, Any>(binding) {
    override fun bind(item: HomeImageItem, itemListener: Any?) {

    }
}

class HomeImageProvider: ViewHolderProvider() {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeImageViewHolder(parent.bind(layoutId()))
}