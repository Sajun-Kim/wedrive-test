package com.namuplanet.base.platfrom

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.google.android.gms.location.Priority

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    protected lateinit var binding: T
    private val requestCodeGenerator = AtomicInteger(5000)
    private val permissionCallbacks = SparseArray<OnRequestPermissionsResult>()
    private val progressDimColor: Int by lazy { "#66000000".toColorInt() }
    private var progress: ViewGroup? = null
    var isViewCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!isViewCreated) {
            binding = DataBindingUtil.inflate(inflater, layoutRes(), container, false)
            binding.lifecycleOwner = this
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isViewCreated) {
            initializeView()

            // 화면 갱신여부를 각자 화면에서 판단
            isViewCreated = !needRefresh()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionCallbacks.get(requestCode)?.let {
            it(grantResults)
            permissionCallbacks.remove(requestCode)
        } ?: super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showProgress(allowBackActivityFinish: Boolean = false) {
        view?.findViewById<ViewGroup>(android.R.id.progress)?.run {
            if (progress == null) {
                progress = createProgress()
                addView(progress)
            } else {
                progress!!.visibility = View.VISIBLE
            }
            visibility = View.VISIBLE
            return
        }

        (requireActivity() as? BaseActivity)?.showProgress(allowBackActivityFinish)
    }

    private fun hideProgress() {
        view?.findViewById<ViewGroup>(android.R.id.progress)?.let {
            progress?.run {
                visibility = View.GONE
                it.removeView(progress)
                progress = null
            }
            it.visibility = View.GONE
            return
        }

        (requireActivity() as? BaseActivity)?.hideProgress()
    }

    fun displayProgress(visible: Boolean, allowBackActivityFinish: Boolean = false) {
        if (visible) showProgress(allowBackActivityFinish) else hideProgress()
    }

    fun changeStatusBarTheme(isLight: Boolean) {
        activity?.run {
            val wic = WindowInsetsControllerCompat(window, window.decorView)
            wic.isAppearanceLightStatusBars = isLight
        }
    }

    private var commonCallback : (() -> Unit)? = null
    private var okCallback     : (() -> Unit)? = null
    private var cancelCallback : (() -> Unit)? = null
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        commonCallback?.invoke()
        when (result.resultCode) {
            RESULT_OK       -> okCallback?.invoke()
            RESULT_CANCELED -> cancelCallback?.invoke()
        }
    }

    fun startActivityLauncher(
        intent: Intent,
        commonCallback : (() -> Unit)? = null,
        okCallback     : (() -> Unit)? = null,
        cancelCallback : (() -> Unit)? = null
    ) {
        this.commonCallback = commonCallback
        this.okCallback     = okCallback
        this.cancelCallback = cancelCallback

        activityResultLauncher.launch(intent)
    }

    fun requestPermissions(
        permissions: Array<String>,
        callback: (grantResults: IntArray) -> Unit
    ) {
        val requestCode = requestCodeGenerator.getAndIncrement()
        permissionCallbacks.put(requestCode, callback)
        requestPermissions(permissions, requestCode)
    }

    private fun createProgress(): ViewGroup {
        return FrameLayout(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(progressDimColor)

            addView(ProgressBar(requireActivity()).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { gravity = Gravity.CENTER }
                indeterminateDrawable.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            })
            visibility = View.VISIBLE
        }
    }

    private var checkBluetoothCallback: (() -> Unit)? = null
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                Toast.makeText(requireContext(), "블루투스가 켜졌습니다.", Toast.LENGTH_SHORT).show()
                checkBluetoothCallback?.invoke()
            }
            else -> Toast.makeText(requireContext(), "블루투스를 켜지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkBluetooth(callback: (() -> Unit)? = null) {
        checkBluetoothCallback = callback

        val bluetoothManager: BluetoothManager? = getSystemService(requireContext(), BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager?.adapter ?: return

        // 블루투스 상태 확인
        when (bluetoothAdapter.isEnabled) {
            // 블루투스 ON 상태
            true -> checkBluetoothCallback?.invoke()
            // 블루투스 OFF 상태
            false -> {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            }
        }
    }

    private var checkLocationCallback: (() -> Unit)? = null
    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            // 사용자가 "사용 설정"을 눌러 위치 서비스를 켰을 때
            Activity.RESULT_OK -> {
                Toast.makeText(requireContext(), "위치 서비스가 켜졌습니다.", Toast.LENGTH_SHORT).show()
                checkLocationCallback?.invoke()
            }
            // 사용자가 "취소"를 눌렀을 때 및 오류 상황
            else -> Toast.makeText(requireContext(), "위치 서비스를 켜지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkLocationSettings(callback: (() -> Unit)? = null) {
        checkLocationCallback = callback

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10_000
        ).build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                // 위치 설정이 켜져 있는 경우
                Timber.i("Location service is already enabled.")
                checkLocationCallback?.invoke()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // 위치 서비스가 꺼져 있는 경우, 다이얼로그 표시
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        locationSettingsLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        Timber.e("Location intent error: ${e.message}")
                    }
                } else {
                    // 설정을 변경할 수 없는 경우
                    Timber.e("Cannot enable location service.")
                }
            }
    }

    fun showViewAboveIme(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
            val imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
//            val imeInsets = windowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.ime())
            // cvNext의 원래 마진/패딩을 고려하여 업데이트
            val initialMarginBottom = 30
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = initialMarginBottom + imeInsets.bottom
            }
            // 또는 패딩을 사용
//             view.updatePadding(bottom = 30 + imeInsets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    @LayoutRes
    abstract fun layoutRes(): Int
    abstract fun initializeView()
    abstract fun observeLiveData()

    open fun viewModel(): BaseViewModel? = null
    open fun needRefresh(): Boolean = false
}

private typealias OnActivityResult = (resultCode: Int, data: Intent?) -> Unit
private typealias OnRequestPermissionsResult = (grantResults: IntArray) -> Unit