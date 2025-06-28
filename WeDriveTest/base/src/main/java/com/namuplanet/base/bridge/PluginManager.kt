package com.namuplanet.base.bridge

import com.namuplanet.base.platfrom.BasePlugin
import kotlin.reflect.KClass

object PluginManager {

    /**
     * 플러그인 사전관리 객체
     */
    private val manager = mutableMapOf<String, KClass<*>>()

    /**
     * 플러그인 추가
     */
    fun add(key: String, pluginClass: KClass<*>) {
        if (key.isEmpty())
            return

        // 플러그인 추가
        manager[key] = pluginClass
    }

    /**
     * 플러그인 반환
     */
    fun get(key: String): BasePlugin? {
        if (key.isEmpty())
            return null

        if (!has(key)) {
            return null
        }

        manager[key]?.let {
            return it.java.getDeclaredConstructor().newInstance() as BasePlugin
        } ?: return null
    }

    fun has(key: String) = manager.containsKey(key)
}