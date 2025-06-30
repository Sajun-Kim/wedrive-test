package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import com.namuplanet.base.extension.bind
import com.namuplanet.base.view.BaseViewHolder
import com.namuplanet.base.view.DisplayableItem
import com.namuplanet.base.view.ViewHolderProvider
import com.wedrive.test.R
import com.wedrive.test.databinding.ItemHomeSearchRecentBinding

private val LAYOUT_ID = R.layout.item_home_search_recent

data class HomeSearchRecentItem(
    val keyword         : String,
    val onCacnelClicked : (String) -> Unit
): DisplayableItem(LAYOUT_ID)

class HomeSearchRecentViewHolder(private val binding: ItemHomeSearchRecentBinding):
    BaseViewHolder<HomeSearchRecentItem, Any>(binding) {
    override fun bind(item: HomeSearchRecentItem, itemListener: Any?) {
        binding.tvKeyword.text = item.keyword

        binding.ivCancel.setOnClickListener {
            item.onCacnelClicked(item.keyword)
        }
    }
}

class HomeSearchRecentProvider: ViewHolderProvider() {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeSearchRecentViewHolder(parent.bind(layoutId()))
}