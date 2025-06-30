package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.namuplanet.base.extension.bind
import com.namuplanet.base.extension.setOnSingleClickListener
import com.namuplanet.base.view.BaseAdapter
import com.namuplanet.base.view.BaseViewHolder
import com.namuplanet.base.view.DisplayableItem
import com.namuplanet.base.view.ItemListener
import com.namuplanet.base.view.ViewHolderProvider
import com.wedrive.test.R
import com.wedrive.test.databinding.ItemHomeSearchBinding

private val LAYOUT_ID = R.layout.item_home_search

data class HomeSearchItem(
    val getSavedKeywords: () -> List<HomeSearchRecentItem>,
    val deleteAllKeywords: () -> Unit,
): DisplayableItem(LAYOUT_ID)

class HomeSearchViewHolder(private val binding: ItemHomeSearchBinding):
    BaseViewHolder<HomeSearchItem, Any>(binding) {
    private val baseAdapter: BaseAdapter by lazy {
        BaseAdapter().apply {
            addHolder(
                holderProvider = HomeSearchRecentProvider(object : ItemListener {
                    override fun onItemDismiss(position: Int) {
                        if (position >= 0 && position < baseAdapter.itemCount)
                            baseAdapter.removeData(listOf(position))
                    }
                })
            )
        }
    }

    override fun bind(item: HomeSearchItem, itemListener: Any?) {
        binding.rvSearch.apply {
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW // 가로 방향 아이템 배치
                flexWrap      = FlexWrap.WRAP     // 아이템이 공간을 벗어나면 다음 줄로 넘김
            }
            adapter = baseAdapter
        }

        binding.tvSearchReset.setOnSingleClickListener {
            item.deleteAllKeywords()
            baseAdapter.clearData()
        }

        baseAdapter.setData(item.getSavedKeywords())
    }
}

class HomeSearchProvider: ViewHolderProvider() {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeSearchViewHolder(parent.bind(layoutId()))
}