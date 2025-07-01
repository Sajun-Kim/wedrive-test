package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import com.bumptech.glide.Glide
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
    val onImageClicked : (String, Int, Int) -> Unit
): DisplayableItem(LAYOUT_ID)

class HomeImageViewHolder(private val binding: ItemHomeImageBinding):
    BaseViewHolder<HomeImageItem, Any>(binding) {
    override fun bind(item: HomeImageItem, itemListener: Any?) {
        // Staggerd Grid Layout 배치를 위해 Image View 크기 미리 설정
        binding.ivImage.layoutParams.width  = item.width
        binding.ivImage.layoutParams.height = item.height

        Glide.with(binding.ivImage)
            .load(item.imageUrl)
            .into(binding.ivImage)

        binding.ivImage.setOnSingleClickListener {
            item.onImageClicked(item.pid, item.width, item.height)
        }
    }
}

class HomeImageProvider: ViewHolderProvider() {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeImageViewHolder(parent.bind(layoutId()))
}