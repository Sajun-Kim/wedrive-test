package com.wedrive.test.feature.login

import android.text.InputFilter
import android.view.inputmethod.EditorInfo
import com.namuplanet.base.extension.createViewModel
import com.namuplanet.base.extension.navigate
import com.namuplanet.base.extension.observe
import com.namuplanet.base.extension.setOnSingleClickListener
import com.namuplanet.base.platfrom.BaseFragment
import com.wedrive.test.R
import com.wedrive.test.databinding.FragmentLoginBinding
import com.wedrive.test.utility.AlphaNumericInputFilter

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override fun layoutRes() = R.layout.fragment_login

    private val viewModel by lazy {
        createViewModel<LoginViewModel>()
    }

    override fun initializeView() {
        // 아이디 입력창에 영문, 숫자만 입력 가능한 필터 적용
        val currentFilters = binding.etId.filters
        val newFilters = mutableListOf<InputFilter>()
        newFilters.addAll(currentFilters)
        newFilters.add(AlphaNumericInputFilter())
        binding.etId.filters = newFilters.toTypedArray()

        // 비밀번호 입력창에서 키보드 완료 버튼 클릭 시 로그인 진행
        binding.etPw.setOnEditorActionListener { textView, actionId, keyEvent ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.login(binding.etId.text.toString(), binding.etPw.text.toString())
                handled = true
            }
            handled
        }

        binding.btnLogin.setOnSingleClickListener {
            viewModel.login(binding.etId.text.toString(), binding.etPw.text.toString())
        }
    }

    override fun observeLiveData() {
        viewModel.isRequestProcessing.observe(this) {
            displayProgress(it)
        }

        viewModel.moveToHome.observe(this) {
            navigate(LoginFragmentDirections.actionLoginToHome())
        }
    }
}