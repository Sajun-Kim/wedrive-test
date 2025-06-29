package com.wedrive.test.feature.home.detail

import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.namuplanet.base.extension.createViewModel
import com.namuplanet.base.extension.popBackStack
import com.namuplanet.base.extension.setOnSingleClickListener
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
        binding.item = viewModel.postDetailItem

        binding.ivBack.setOnSingleClickListener {
            popBackStack()
        }

        viewModel.initHomeDetail(args.pid)
    }

    override fun observeLiveData() {
        viewModel.isRequestProcessing.observe(this) {
            displayProgress(it)
        }
        viewModel.postDetailItem.cover.observe(this) {
            if (it.isNotEmpty()) {
                Timber.d("image url: $it")

                Glide.with(binding.ivImage)
                    .load(it)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(24)))
                    .into(binding.ivImage)

                binding.ivImage.minimumHeight = 1
            }
        }
        viewModel.postDetailItem.profile.observe(this) {
            if (it.isNotEmpty()) {
                Timber.d("profile url: $it")

                Glide.with(binding.ivProfile)
                    .load(it)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.ivProfile)
            }
        }
    }
}