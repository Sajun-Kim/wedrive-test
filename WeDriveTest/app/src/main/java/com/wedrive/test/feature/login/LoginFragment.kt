package com.wedrive.test.feature.login

import com.namuplanet.base.platfrom.BaseFragment
import com.wedrive.test.R
import com.wedrive.test.WeDriveTestApplication
import com.wedrive.test.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override fun layoutRes() = R.layout.fragment_login

    override fun initializeView() {
        WeDriveTestApplication.instance.showToast("로그인 화면 진입")
    }

    override fun observeLiveData() {

    }
}