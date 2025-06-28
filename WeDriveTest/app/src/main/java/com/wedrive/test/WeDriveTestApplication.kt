package com.wedrive.test

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import com.namuplanet.base.preference.CustomSharedPreferences
import timber.log.Timber

class WeDriveTestApplication: Application() {
    companion object {
        lateinit var instance : WeDriveTestApplication
    }

    lateinit var pref: CustomSharedPreferences

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

    fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}

object PrefKeys {
    const val USER_ID = "userId"
}