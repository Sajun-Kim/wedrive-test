package com.wedrive.test

import android.app.Application
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

    fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}

object PrefKeys {
    const val USER_ID = "userId"
}