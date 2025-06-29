package com.wedrive.test.feature.home

import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.namuplanet.base.extension.createViewModel
import com.namuplanet.base.extension.navigate
import com.namuplanet.base.extension.observe
import com.namuplanet.base.platfrom.BaseFragment
import com.namuplanet.base.view.BaseAdapter
import com.wedrive.test.R
import com.wedrive.test.databinding.FragmentHomeBinding
import com.wedrive.test.feature.home.viewholder.HomeImageProvider

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun layoutRes() = R.layout.fragment_home

    private val viewModel by lazy {
        createViewModel<HomeViewModel>()
    }

    private val baseAdapter: BaseAdapter by lazy {
        BaseAdapter().apply {
            addHolder(HomeImageProvider())
        }
    }

    override fun initializeView() {
        binding.rvHome.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }
            adapter = baseAdapter
        }

        // 포커스 여부에 따른 힌트 표시 조정
        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            when (hasFocus) {
                true  -> binding.etSearch.hint = ""
                false -> binding.etSearch.hint = getString(R.string.home_search_hint)
            }
        }

        viewModel.initHome()
    }

    override fun observeLiveData() {
        viewModel.isRequestProcessing.observe(this) {
            displayProgress(it)
        }
        viewModel.showItems.observe(this) {
            baseAdapter.setData(it)
        }
        viewModel.moveToHomeDetail.observe(this) {
            navigate(HomeFragmentDirections.actionHomeToDetail(it))
        }
    }
}