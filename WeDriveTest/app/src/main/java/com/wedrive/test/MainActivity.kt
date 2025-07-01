package com.wedrive.test

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.namuplanet.base.platfrom.BaseActivity
import com.namuplanet.base.platfrom.OnBackPressedListener
import com.wedrive.test.databinding.ActivityMainBinding
import com.wedrive.test.feature.home.HomeFragment
import timber.log.Timber

class MainActivity : BaseActivity() {
    private lateinit var binding       : ActivityMainBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 켜짐 유지
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 뒤로가기 이벤트 등록
        onBackPressedRegister()

        // 기기의 가로/세로 길이 조회
        getDeviceWidthHeight()

        initializeView()
    }

    private fun initializeView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph)

        setEdgeToEdgeView(binding.lyMain)
    }

    private fun getDeviceWidthHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            val width = bounds.width()
            val height = bounds.height()
            Timber.d("WindowMetrics: Width = $width, Height = $height")
            WeDriveTestApplication.instance.setDeviceWidthHeight(width, height)
        }
        else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            this.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            Timber.d("DisplayMetrics: Width = $width, Height = $height")
            WeDriveTestApplication.instance.setDeviceWidthHeight(width, height)
        }
    }

    private fun onBackPressedRegister() {
        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // onBackPressed() 이벤트 처리
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_fragment)
                val fragment = navHostFragment?.childFragmentManager?.fragments?.first()

                if (fragment is OnBackPressedListener && fragment.onBackPressed()) {
                    return
                }

                // 홈 화면 더블 클릭 종료 처리
                if (fragment is HomeFragment) {
                    finishAffinity()
                    return
                }

                // 기본 동작
                navController.navigateUp()
            }
        })
    }
}