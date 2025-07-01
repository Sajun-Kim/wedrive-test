package com.wedrive.test.feature.home.detail

import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.namuplanet.base.extension.createViewModel
import com.namuplanet.base.extension.popBackStack
import com.namuplanet.base.extension.setOnSingleClickListener
import com.namuplanet.base.platfrom.BaseFragment
import com.wedrive.test.R
import com.wedrive.test.databinding.FragmentHomeDetailBinding

class HomeDetailFragment : BaseFragment<FragmentHomeDetailBinding>() {
    override fun layoutRes() = R.layout.fragment_home_detail

    private val viewModel by lazy {
        createViewModel<HomeDetailViewModel>()
    }

    private val args by navArgs<HomeDetailFragmentArgs>()

    override fun initializeView() {
        binding.item = viewModel.postDetailItem

        binding.ivBack.setOnSingleClickListener {
            popBackStack()
        }

        viewModel.initHomeDetail(args.pid)
    }

    override fun observeLiveData() {
        viewModel.postDetailItem.cover.observe(this) {
            if (it.isNotEmpty()) {
                binding.ivImage.layoutParams.width = args.width * 2
                binding.ivImage.layoutParams.height = args.height * 2

                Glide.with(binding.ivImage)
                    .load(it)
                    .into(binding.ivImage)
            }
        }
        viewModel.postDetailItem.profile.observe(this) {
            if (it.isNotEmpty()) {
                Glide.with(binding.ivProfile)
                    .load(it)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.ivProfile)
            }
        }
    }
}