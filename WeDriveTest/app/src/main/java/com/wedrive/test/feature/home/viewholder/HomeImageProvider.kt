package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.namuplanet.base.extension.bind
import com.namuplanet.base.extension.setOnSingleClickListener
import com.namuplanet.base.view.BaseViewHolder
import com.namuplanet.base.view.DisplayableItem
import com.namuplanet.base.view.ViewHolderProvider
import com.wedrive.test.R
import com.wedrive.test.databinding.ItemHomeImageBinding

private val LAYOUT_ID = R.layout.item_home_image

data class HomeImageItem(
    val pid            : String,
    val imageUrl       : String,
    val width          : Int,
    val height         : Int,
    val onImageClicked : (String) -> Unit
): DisplayableItem(LAYOUT_ID)

class HomeImageViewHolder(private val binding: ItemHomeImageBinding):
    BaseViewHolder<HomeImageItem, Any>(binding) {
    override fun bind(item: HomeImageItem, itemListener: Any?) {
        binding.ivImage.layoutParams.width = item.width
        binding.ivImage.layoutParams.height = item.height

        Glide.with(binding.ivImage)
            .load(item.imageUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(binding.ivImage)

        binding.ivImage.setOnSingleClickListener {
            item.onImageClicked(item.pid)
        }
    }
}

class HomeImageProvider: ViewHolderProvider() {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeImageViewHolder(parent.bind(layoutId()))
}