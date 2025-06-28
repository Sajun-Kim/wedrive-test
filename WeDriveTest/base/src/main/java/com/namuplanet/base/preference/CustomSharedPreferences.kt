package com.namuplanet.base.preference

import android.content.Context
import android.content.SharedPreferences
import com.namuplanet.base.json.BigDecimalJsonAdapter
import com.squareup.moshi.Moshi
import timber.log.Timber

/**
 * Customized [SharedPreferences] to use it easily.
 */
class CustomSharedPreferences(
    context: Context,
    prefFileName: String
) {
    val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    /**
     * Checks whether the preferences contains a preference.
     *
     * @param key The name of the preference to check.
     * @return Returns true if the preference exists in the preferences,
     *         otherwise false.
     */
    fun contains(key: String): Boolean = sharedPreferences.contains(key)

    fun clearAll() {
        sharedPreferences.edit().clear().commit()
    }

    /**
     * Retrieve a String, Int, Long, Float, Boolean, Set<String> and
     * data class annotated @JsonClass(generateAdapter = true)
     * value from the preferences.
     *
     * We can use it like below
     *
     * val value: T = preferences[key]
     * val value = preferences[key, false]
     * val value = preferences[key, defValue, DataClass::class.java]
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     *
     * @return Returns the preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that is not
     * the defined type. Throws
     * IllegalArgumentException if there is an error with JsonAdapter.fromJson().
     *
     * @throws ClassCastException
     * @throws IllegalArgumentException
     */
    inline operator fun <reified T : Any> get(
        key: String,
        defValue: T? = null,
        type: Class<T>? = null
    ): T {
        return when (val typeClass = type ?: T::class.java) {
            java.lang.String::class.java ->
                sharedPreferences.getString(key, defValue as? String ?: "") as T
            java.lang.Integer::class.java ->
                sharedPreferences.getInt(key, defValue as? Int ?: -1) as T
            java.lang.Long::class.java ->
                sharedPreferences.getLong(key, defValue as? Long ?: -1) as T
            java.lang.Float::class.java ->
                sharedPreferences.getFloat(key, defValue as? Float ?: -1f) as T
            java.lang.Boolean::class.java ->
                sharedPreferences.getBoolean(key, defValue as? Boolean ?: false) as T
            Set::class.java -> {
                @Suppress("UNCHECKED_CAST")
                sharedPreferences.getStringSet(key, defValue as? Set<String> ?: emptySet()) as T
            }
            else -> {
                try {
                    getTypedObject(key, defValue, typeClass) as T
                } catch (e: IllegalArgumentException) {
                    Timber.e("failed to get value for key: $key")
                    "error" as T
                }
            }
        }
    }

    /**
     * Set any value in the preferences editor
     *
     * We can use it like below
     *
     * preferences[key] = value
     * preferences[key] = null
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     *
     * Throws IllegalArgumentException if there is an error with JsonAdapter.toJson().
     *
     * @throws IllegalArgumentException
     */
    @Synchronized
    operator fun set(key: String, value: Any?) = when (value) {
        null ->
            edit { it.remove(key) }
        is String ->
            edit { it.putString(key, value) }
        is Int ->
            edit { it.putInt(key, value) }
        is Long ->
            edit { it.putLong(key, value) }
        is Float ->
            edit { it.putFloat(key, value) }
        is Boolean ->
            edit { it.putBoolean(key, value) }
        else -> Unit
    }

    /**
     * Set any value in the preferences editor
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     *
     * Throws IllegalArgumentException if there is an error with JsonAdapter.toJson().
     *
     * @throws IllegalArgumentException
     */
    @Synchronized
    fun setCommit(key: String, value: Any?): Boolean {
        return when (value) {
            null ->
                editCommit { it.remove(key) }
            is String ->
                editCommit { it.putString(key, value) }
            is Int ->
                editCommit { it.putInt(key, value) }
            is Long ->
                editCommit { it.putLong(key, value) }
            is Float ->
                editCommit { it.putFloat(key, value) }
            is Boolean ->
                editCommit { it.putBoolean(key, value) }
            else -> false
        }
    }

    @Throws(IllegalArgumentException::class)
    inline fun <reified T : Any> getTypedObject(
        key: String,
        defValue: T?,
        type: Class<T>
    ): T? =
        try {
            sharedPreferences.getString(key, null)?.let { value ->
                Moshi.Builder().add(BigDecimalJsonAdapter()).build().adapter(type).fromJson(value)
            } ?: defValue
        } catch (e: com.squareup.moshi.JsonDataException) {
            Timber.w("Json parsing failed. Remove data for $key.")
            Timber.e(e)
            set(key, null)
            null
        } catch (e: Throwable) {
            defValue ?: throw IllegalArgumentException()
        }

    private inline fun edit(execute: (SharedPreferences.Editor) -> Unit) =
        sharedPreferences.edit().also(execute).apply()

    // 로그아웃 이슈로 인한 추가 - 사용 지양
    private inline fun editCommit(execute: (SharedPreferences.Editor) -> Unit): Boolean {
        return sharedPreferences.edit().also(execute).commit()
    }
}
