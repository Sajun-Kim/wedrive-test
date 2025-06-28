package com.namuplanet.base.platfrom

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider

@Navigator.Name("navigation")
class CustomGraphNavigator(private val navigatorProvider: NavigatorProvider) :
    NavGraphNavigator(navigatorProvider) {

    @Throws(IllegalStateException::class)
    override fun navigate(
        destination: NavGraph,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (navigatorExtras is Extras) {
            val destinationId = navigatorExtras.destinationId
            val startDestination = destination.findNode(destinationId)
                ?: throw IllegalStateException("invalid destination id")
            val navigator = navigatorProvider.getNavigator<Navigator<NavDestination>>(
                startDestination.navigatorName
            )
            return navigator.navigate(startDestination, args, navOptions, navigatorExtras)
        } else {
            return super.navigate(
                destination,
                args,
                navOptions,
                navigatorExtras
            )
        }
    }

    data class Extras(@IdRes val destinationId: Int) : Navigator.Extras
}