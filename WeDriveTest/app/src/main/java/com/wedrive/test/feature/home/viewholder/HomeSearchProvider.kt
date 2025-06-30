package com.wedrive.test.feature.home.viewholder

import android.view.ViewGroup
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.namuplanet.base.extension.bind
import com.namuplanet.base.view.BaseAdapter
import com.namuplanet.base.view.BaseViewHolder
import com.namuplanet.base.view.DisplayableItem
import com.namuplanet.base.view.ViewHolderProvider
import com.wedrive.test.R
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.databinding.ItemHomeSearchBinding
import com.wedrive.test.utility.sqlite.SQLiteManager

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
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW // 가로 방향 아이템 배치
                flexWrap      = FlexWrap.WRAP     // 아이템이 공간을 벗어나면 다음 줄로 넘김
            }
            adapter = baseAdapter
        }

        showSavedKeywords()
    }

    val sqliteManager = SQLiteManager(WeDriveTestApplication.instance.applicationContext)

    fun showSavedKeywords() {
        val items = mutableListOf<HomeSearchRecentItem>()
        val cursor = sqliteManager.getAllKeyword()
        if (cursor.moveToFirst()) {
            do {
                val keyword = cursor.getStringOrNull(cursor.getColumnIndex("keyword"))
                if (keyword != null) {
                    items.add(HomeSearchRecentItem(keyword, {}))
                }
            } while (cursor.moveToNext())
        }

        baseAdapter.setData(items)
    }
}

class HomeSearchProvider: ViewHolderProvider() {
    override fun layoutId() = LAYOUT_ID
    override fun create(parent: ViewGroup) = HomeSearchViewHolder(parent.bind(layoutId()))
}