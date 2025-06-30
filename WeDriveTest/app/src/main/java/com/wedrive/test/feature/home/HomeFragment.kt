package com.wedrive.test.feature.home

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.namuplanet.base.extension.createViewModel
import com.namuplanet.base.extension.navigate
import com.namuplanet.base.platfrom.BaseFragment
import com.namuplanet.base.platfrom.OnBackPressedListener
import com.namuplanet.base.view.BaseAdapter
import com.namuplanet.base.view.displayableItems
import com.wedrive.test.R
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.databinding.FragmentHomeBinding
import com.wedrive.test.feature.home.viewholder.HomeImageProvider
import com.wedrive.test.feature.home.viewholder.HomeSearchItem
import com.wedrive.test.feature.home.viewholder.HomeSearchProvider
import com.wedrive.test.utility.ViewUtil
import com.wedrive.test.utility.sqlite.SQLiteManager

class HomeFragment : BaseFragment<FragmentHomeBinding>(), OnBackPressedListener {
    override fun layoutRes() = R.layout.fragment_home

    private val viewModel by lazy {
        createViewModel<HomeViewModel>()
    }

    private val baseAdapter: BaseAdapter by lazy {
        BaseAdapter().apply {
            addHolder(HomeImageProvider())
            addHolder(HomeSearchProvider())
        }
    }

    private val appContext = WeDriveTestApplication.instance.applicationContext

    val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
        gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
    }
    val linearLayoutManager = LinearLayoutManager(appContext)

    val sqliteManager = SQLiteManager(appContext)

    override fun initializeView() {
        binding.rvHome.apply {
            layoutManager = staggeredGridLayoutManager
            adapter = baseAdapter
        }

        // 포커스 여부에 따른 힌트 표시 조정
        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            when (hasFocus) {
                true -> {
                    // 검색창 힌트 변경
                    binding.etSearch.hint = ""

                    // 검색창 마진, Visibility 변경
                    ViewUtil.changeConstraintMargin(
                        context  = requireContext(),
                        layout   = binding.lyMain,
                        view     = binding.etSearch,
                        anchor   = ConstraintSet.END,
                        marginDp = 54
                    )
                    binding.tvCancel.visibility = View.VISIBLE

                    // 검색창 아래 화면 변경
                    binding.rvHome.layoutManager = linearLayoutManager
                    baseAdapter.setData(
                        displayableItems {
                            +HomeSearchItem (
                                getSavedKeywords = { viewModel.getSavedKeywords() },
                                deleteAllKeywords = { viewModel.deleteAllKeywords() }
                            )
                        }
                    )
                }
                false -> {
                    binding.etSearch.hint = getString(R.string.home_search_hint)

                    ViewUtil.changeConstraintMargin(
                        context  = requireContext(),
                        layout   = binding.lyMain,
                        view     = binding.etSearch,
                        anchor   = ConstraintSet.END,
                        marginDp = 10
                    )
                    binding.tvCancel.visibility = View.GONE

                    binding.rvHome.layoutManager = staggeredGridLayoutManager
                    viewModel.getCoverImages()
                }
            }
        }

        // 취소 클릭 시
        binding.tvCancel.setOnClickListener {
            binding.etSearch.clearFocus()
            binding.etSearch.setText("")
            hideKeyboard(binding.tvCancel)

//            ViewUtil.changeConstraintMargin(
//                context  = requireContext(),
//                layout   = binding.lyMain,
//                view     = binding.etSearch,
//                anchor   = ConstraintSet.END,
//                marginDp = 10
//            )
//            binding.tvCancel.visibility = View.GONE
//
//            binding.rvHome.layoutManager = staggeredGridLayoutManager
//            viewModel.getCoverImages()
        }

        // 키보드 검색(엔터) 눌렀을 때 동작 설정
        binding.etSearch.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.getCoverImages(view.text.toString())
                hideKeyboard(view)
                sqliteManager.insertOrUpdateKeyword(view.text.toString())
                return@setOnEditorActionListener true
            }
            else
                false
        }

        viewModel.getCoverImages()
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun observeLiveData() {
        viewModel.isRequestProcessing.observe(this) {
            displayProgress(it)
        }
        viewModel.showItems.observe(this) {
            baseAdapter.setData(it)
        }
        viewModel.moveToHomeDetail.observe(this) {
            navigate(HomeFragmentDirections.actionHomeToDetail(it.first, it.second, it.third))
        }
    }

    private var backPressedTime = 0L
    override fun onBackPressed(): Boolean {
        // 2초 내 취소 2번 누르면 종료
        if (System.currentTimeMillis() > backPressedTime + 2_000L) {
            backPressedTime = System.currentTimeMillis()
            WeDriveTestApplication.instance.showToast(getString(R.string.common_exit_confirm))
            return true
        }
        else {
            return false
        }
    }
}