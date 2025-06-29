package com.wedrive.test

import android.app.Application
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import com.namuplanet.base.preference.CustomSharedPreferences
import timber.log.Timber
import kotlin.system.exitProcess

class WeDriveTestApplication: Application() {
    companion object {
        lateinit var instance : WeDriveTestApplication
    }

    lateinit var pref: CustomSharedPreferences
    private var deviceWidth  : Int = 0
    private var deviceHeight : Int = 0

    override fun onCreate() {
        super.onCreate()
        instance = this

        // SharedPreferences
        pref = CustomSharedPreferences(applicationContext, getString(R.string.app_pref_name))

        // Timber
        Timber.plant(Timber.DebugTree())
    }

    // 버전 가져오기
    fun getAppVersionName(): String {
        return try {
            val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                applicationContext.packageManager.getPackageInfo(applicationContext.packageName, PackageManager.PackageInfoFlags.of(0))
            else
                applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)

            packageInfo.versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e("get version name error\n${e.stackTraceToString()}")
            ""
        }
    }

    fun setDeviceWidthHeight(width: Int, height: Int) {
        deviceWidth  = width
        deviceHeight = height
    }
    fun getDeviceWidthHeight(): Pair<Int, Int> {
        return deviceWidth to deviceHeight
    }

    fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    // 앱 재시작
    // 새로운 액티비티 생성 후 기존 액티비티 종료
    fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let {
            startActivity(it)
            exitProcess(0)
        }
    }
}

object PrefKeys {
    const val USER_ID = "userId"
}