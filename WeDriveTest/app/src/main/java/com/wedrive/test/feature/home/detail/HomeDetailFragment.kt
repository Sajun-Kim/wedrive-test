package com.wedrive.test.feature.home.detail

import androidx.navigation.fragment.navArgs
import com.namuplanet.base.extension.createViewModel
import com.namuplanet.base.platfrom.BaseFragment
import com.wedrive.test.R
import com.wedrive.test.databinding.FragmentHomeDetailBinding
import timber.log.Timber

class HomeDetailFragment : BaseFragment<FragmentHomeDetailBinding>() {
    override fun layoutRes() = R.layout.fragment_home_detail

    private val viewModel by lazy {
        createViewModel<HomeDetailViewModel>()
    }

    private val args by navArgs<HomeDetailFragmentArgs>()

    override fun initializeView() {
        Timber.d("pid: ${args.pid}")
        viewModel.initHomeDetail(args.pid)
    }

    override fun observeLiveData() {
        viewModel.isRequestProcessing.observe(this) {
            displayProgress(it)
        }
    }
}