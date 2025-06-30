package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import com.namuplanet.base.extension.bind
import com.namuplanet.base.extension.setOnSingleClickListener
import com.namuplanet.base.view.BaseViewHolder
import com.namuplanet.base.view.DisplayableItem
import com.namuplanet.base.view.ItemListener
import com.namuplanet.base.view.ViewHolderProvider
import com.wedrive.test.R
import com.wedrive.test.databinding.ItemHomeSearchRecentBinding

private val LAYOUT_ID = R.layout.item_home_search_recent

data class HomeSearchRecentItem(
    val keyword         : String,
    val onItemClicked   : (String) -> Unit,
    val onCancelClicked : (String) -> Unit
): DisplayableItem(LAYOUT_ID)

class HomeSearchRecentViewHolder(private val binding: ItemHomeSearchRecentBinding, private val listener: ItemListener?):
    BaseViewHolder<HomeSearchRecentItem, Any>(binding) {
    override fun bind(item: HomeSearchRecentItem, itemListener: Any?) {
        binding.tvKeyword.text = item.keyword

        binding.lyHomeTop.setOnSingleClickListener {
            item.onItemClicked(item.keyword)
        }

        binding.ivCancel.setOnClickListener {
            // DB에서 제거
            item.onCancelClicked(item.keyword)

            // 화면에서 제거
            val position = bindingAdapterPosition
            listener?.onItemDismiss(position)
        }
    }
}

class HomeSearchRecentProvider(itemListener: ItemListener?): ViewHolderProvider(itemListener) {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeSearchRecentViewHolder(parent.bind(layoutId()), itemListener)
}