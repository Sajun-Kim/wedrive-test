package com.namuplanet.base.extension

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.namuplanet.base.platfrom.*
import timber.log.Timber
import kotlin.reflect.KClass

fun <T : ViewDataBinding> Fragment.inflateWithNoParent(layoutRes: Int): T {
    return DataBindingUtil.inflate(
        requireActivity().layoutInflater,
        layoutRes,
        null,
        false
    )
}

inline fun <reified T : ViewModel> Fragment.createViewModel(): T {
    return ViewModelProvider(this)[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.createViewModelLazy(): Lazy<T> {
    return ViewModelLazy(T::class, this)
}

inline fun <reified T : ViewModel> Fragment.createActivityViewModel(): T {
    return ViewModelProvider(requireActivity())[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.createSharedViewModel(
    key: DlStoreKey
): T {
    return (activity as? BaseActivity)?.getSharedViewModelStore()?.let {
        ViewModelProvider(it.get(key), ViewModelProvider.NewInstanceFactory()).get(T::class.java)
    } ?: createActivityViewModel()
}

fun Fragment.clearSharedViewModelStore(
    key: DlStoreKey
) {
    (activity as? BaseActivity)?.getSharedViewModelStore()?.clearLbSharedViewModelStore(key)
}

inline fun <reified T : ViewModel> Fragment.createParentViewModel(): T {
    return ViewModelProvider(parentFragment ?: this)[T::class.java]
}

class ViewModelLazy<VM : ViewModel>(
    private val viewModelClass: KClass<VM>,
    private val fragment: Fragment
) : Lazy<VM> {
    private var cached: VM? = null
    override val value: VM
        get() {
            return cached
                ?: ViewModelProvider(fragment)[viewModelClass.java].also { cached = it }
        }

    override fun isInitialized() = cached != null
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.createViewModel(
    crossinline func: () -> T
): T {
    return ViewModelProvider(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>): T = func() as T
    })[T::class.java]
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.createActivityViewModel(
    crossinline func: () -> T
): T {
    return ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>): T = func() as T
    })[T::class.java]
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.createParentViewModel(
    crossinline func: () -> T
): T {
    return ViewModelProvider(parentFragment ?: this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>): T = func() as T
    })[T::class.java]
}

inline fun <T : Fragment> T.withArgs(
    argsBuilder: Bundle.() -> Unit
): T =
    this.apply {
        arguments = Bundle().apply(argsBuilder)
    }

fun Fragment.navigateAndPopupTo(
    @IdRes navigateDestinationId: Int,
    @IdRes popupDestinationId: Int
) {
    val navOptions = NavOptions.Builder().setPopUpTo(popupDestinationId, true).build()
    findNavController().navigate(navigateDestinationId, null, navOptions)
}

fun Fragment.navigateAndPopupTo(
    directions: NavDirections,
    @IdRes popupDestinationId: Int
) {
    val navOptions = NavOptions.Builder().setPopUpTo(popupDestinationId, true).build()
    findNavController().navigate(directions, navOptions)
}

fun Fragment.navigateAndPopupTo(
    @IdRes navigateDestinationId: Int,
    @IdRes popupDestinationId: Int,
    inclusive: Boolean = true,
    bundle: Bundle? = null,
    @IdRes navGraphId: Int? = null
) {
    val navOptions = NavOptions.Builder().setPopUpTo(popupDestinationId, inclusive).build()
    navGraphId?.let {
        val extras = CustomGraphNavigator.Extras(navigateDestinationId)
        findNavController().navigate(navGraphId, bundle, navOptions, extras)
    } ?: findNavController().navigate(navigateDestinationId, bundle, navOptions)
}

fun Fragment.navigateAndPopupCurrent(@IdRes destinationId: Int, args: Bundle? = null) {
    val popupDestinationId = findNavController().currentDestination?.id ?: -1
    val navOptions = NavOptions.Builder().setPopUpTo(popupDestinationId, true).build()
    findNavController().navigate(destinationId, args, navOptions)
}

// 이전 stack으로 이동
fun Fragment.navigateUp() {
    findNavController().navigateUp()
}

// 현재 stack pop, 이전 stack 존재시 해당 stack으로 이동
fun Fragment.popBackStack() {
    findNavController().popBackStack()
}

fun Fragment.navigateOut() {
    findNavController().run {
        currentDestination?.parent?.id
            ?.let { graphId -> popBackStack(graphId, true) }
            ?: navigateUp()
    }
}

fun Fragment.navigate(
    @IdRes destinationId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null
) {
    findNavController().navigate(destinationId, args, navOptions)
}

fun Fragment.navigate(directions: NavDirections, navOptions: NavOptions? = null) {
    findNavController().navigate(directions, navOptions)
}

fun Fragment.navigate(@IdRes navGraphId: Int, @IdRes destinationId: Int, bundle: Bundle? = null) {
    val extras = CustomGraphNavigator.Extras(destinationId)
    findNavController().navigate(navGraphId, bundle, null, extras)
}

fun Fragment.navigateBack() {
    val destinationId = findNavController().currentDestination?.id ?: -1
    parentFragmentManager.onBackStackChanged {
        it.removeOnBackStackChangedListener(this)
        (it.fragments[0] as? NavigationResult)?.onNavigationResult(destinationId, Bundle())
    }
    findNavController().popBackStack()
}
fun Fragment.navigateBackWithRefresh(result: Bundle? = null) {
    val destinationId = findNavController().currentDestination?.id ?: -1

    parentFragmentManager.onBackStackChanged {
        it.removeOnBackStackChangedListener(this)
        (it.fragments[0] as? BaseFragment<*>)?.isViewCreated = false
        result?.let { bundle ->
            (it.fragments[0] as? NavigationResult)?.onNavigationResult(destinationId, bundle)
        }
    }
    findNavController().popBackStack()
}

fun Fragment.navigateBackWithResult(result: Bundle = Bundle(), refresh: Boolean = false) {
    val destinationId = findNavController().currentDestination?.id ?: -1
    val destinationLabel = findNavController().currentDestination?.label ?: "[NO_DESTINATION]"
    parentFragmentManager.onBackStackChanged {
        it.removeOnBackStackChangedListener(this)
        (it.fragments[0] as? BaseFragment<*>)?.isViewCreated = refresh.not()
        (it.fragments[0] as? NavigationResult)?.onNavigationResult(destinationId, result)
        Timber.d("onNavigationResult $destinationLabel -> $result}")
    }
    findNavController().popBackStack()
}

fun Fragment.navigateWithBundle(@IdRes destinationId: Int, args: Bundle) {
    findNavController().navigate(destinationId, args)
}
