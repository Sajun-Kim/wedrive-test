package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.namuplanet.base.extension.bind
import com.namuplanet.base.view.BaseAdapter
import com.namuplanet.base.view.BaseViewHolder
import com.namuplanet.base.view.DisplayableItem
import com.namuplanet.base.view.ViewHolderProvider
import com.namuplanet.base.view.displayableItems
import com.wedrive.test.R
import com.wedrive.test.databinding.ItemHomeSearchBinding

private val LAYOUT_ID = R.layout.item_home_search

data class HomeSearchItem(
    val tmp: String = "",
): DisplayableItem(LAYOUT_ID)

class HomeSearchViewHolder(private val binding: ItemHomeSearchBinding):
    BaseViewHolder<HomeSearchItem, Any>(binding) {
    private val baseAdapter: BaseAdapter by lazy {
        BaseAdapter().apply {
            addHolder(HomeSearchRecentProvider())
        }
    }

    override fun bind(item: HomeSearchItem, itemListener: Any?) {
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = baseAdapter
        }

        showRecentSearchKeywords()
    }

    fun showRecentSearchKeywords() {
        baseAdapter.setData(
            displayableItems {
                +HomeSearchRecentItem("검정색", {})
            }
        )
    }
}

class HomeSearchProvider: ViewHolderProvider() {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeSearchViewHolder(parent.bind(layoutId()))
}