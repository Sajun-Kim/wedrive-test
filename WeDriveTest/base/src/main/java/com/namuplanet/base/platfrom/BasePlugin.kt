package com.namuplanet.base.platfrom

import org.json.JSONObject

abstract class BasePlugin {

    protected var fragment: BaseFragment<*>? = null
    protected lateinit var action: String
    protected var args: JSONObject? = null
    protected lateinit var callbackFunc: String

    /**
     * 결과 반환 함수 포인터
     * callback: String : 스크립트 콜백 함수 명
     * result: JSONObject : 결과 데이터
     * {
     *     code : {API 서버와 동일},
     *     message : {API 서버와 동일},
     *     result: { }
     * }
     */
    protected var result: ((String, JSONObject) -> Unit)? = null

    fun setFragment(f: BaseFragment<*>?): BasePlugin{
        fragment = f
        return this
    }

    /**
     * 결재에 사용된 리소스를 제거하고 웹에 결과를 전달합니다.
     */
    open fun setPluginResult (args: JSONObject) {
        // 결과 전달
        this.result?.invoke(callbackFunc, args)
    }

    abstract fun execute(action: String, args: JSONObject?, callbackFunc: String, result: ((String, JSONObject) -> Unit)?)

    companion object {
        const val RESULT_CODE_SUCCESS = "000"
        const val RESULT_CODE_NOTCONNECTED = "001"
        const val RESULT_CODE_EMPTY_SKU = "002"
        const val RESULT_CODE_CANCEL = "003"
        const val RESULT_CODE_ETC = "999"

        const val RESULT_FIELD_CODE = "code"
        const val RESULT_FIELD_MESSAGE = "message"
        const val RESULT_FIELD_RESULT = "result"
    }
}