package com.namuplanet.base.platfrom

import android.os.Bundle
import androidx.annotation.IdRes

interface NavigationResult {
    fun onNavigationResult(@IdRes destinationId: Int, result: Bundle)
}