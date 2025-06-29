package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.namuplanet.base.extension.bind
import com.namuplanet.base.view.BaseViewHolder
import com.namuplanet.base.view.DisplayableItem
import com.namuplanet.base.view.ViewHolderProvider
import com.wedrive.test.R
import com.wedrive.test.databinding.ItemHomeImageBinding

private val LAYOUT_ID = R.layout.item_home_image

data class HomeImageItem(
    val imageUrl : String,
    val width    : Int,
    val height   : Int
): DisplayableItem(LAYOUT_ID)

class HomeImageViewHolder(private val binding: ItemHomeImageBinding):
    BaseViewHolder<HomeImageItem, Any>(binding) {
    override fun bind(item: HomeImageItem, itemListener: Any?) {
        binding.ivImage.minimumHeight = item.height - 100

        Glide.with(binding.ivImage)
            .load(item.imageUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(24)))
            .into(binding.ivImage)
    }
}

class HomeImageProvider: ViewHolderProvider() {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeImageViewHolder(parent.bind(layoutId()))
}