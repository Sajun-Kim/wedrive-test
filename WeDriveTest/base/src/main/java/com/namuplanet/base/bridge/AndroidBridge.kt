package com.namuplanet.base.bridge

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.namuplanet.base.platfrom.BaseFragment
import org.json.JSONObject
import timber.log.Timber

class AndroidBridge(
    private val webCommon: WebView,
    private val fragment: BaseFragment<*>,
    private val manager: PluginManager
) {
    private val handler: Handler = Handler(Looper.getMainLooper())

    /**
     * 플러그인 결과 콜백 실행
     */
    private fun onResult (callback:String, result: JSONObject) {
        if (callback.isEmpty())
            return

        webCommon.evaluateJavascript("$callback($result)", null)
    }

    /**
     * 웹에서 전송한 문자열 데이터 입니다.
     */
    @JavascriptInterface
    fun execute(jsonArgs: String) = with(fragment) {
        if (jsonArgs.isEmpty()) {
            return@with
        }
        val param = JSONObject(jsonArgs)
        val clazz = param.optString(CLAZZ)
        val action = param.optString(ACTION)
        val callback = param.optString(CALLBACK)
        val args = param.optJSONObject(ARGS)

        // js 에서 요청된 플러그인 존재 여부 확인
        if (!manager.has(clazz)) {
            return@with
        }

        // js 에서 요청된 플러그인 실행
        handler.post {
            manager.get(clazz)
                ?.setFragment(this)
                ?.execute(
                    action,
                    args,
                    callback,
                    ::onResult
                ) ?: Timber.d("The plugin does not exist.")
        }
    }

    companion object {
        private const val CLAZZ = "clazz"
        private const val ACTION = "action"
        private const val CALLBACK = "callback"
        private const val ARGS = "args"
    }
}