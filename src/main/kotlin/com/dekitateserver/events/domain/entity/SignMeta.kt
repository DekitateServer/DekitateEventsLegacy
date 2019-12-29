package com.dekitateserver.events.domain.entity

import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import kotlin.collections.set

class SignMeta(
        val location: Location,
        val key: Int? = null,
        dataMap: Map<String, Any>? = null
) {
    private val _dataMap: MutableMap<String, Any> = dataMap?.toMutableMap() ?: mutableMapOf()
    val dataMap: Map<String, Any>
        get() = _dataMap.toMap()

    fun getIntOrError(key: String): Int? = getOrErrorInternal<Int>(key)

    fun getStringOrError(key: String): String? = getOrErrorInternal<String>(key)

    fun <T : ConfigurationSerializable> getOrError(key: String): T? = getOrErrorInternal<T>(key)

    @Suppress("UNCHECKED_CAST")
    private fun <T> getOrErrorInternal(key: String): T? = with(_dataMap) {
        if (!containsKey(key)) {
            Log.error("SignMeta($location)に'$key'がありません")
            return null
        }

        return get(key) as T
    }

    fun put(key: String, value: Int) {
        _dataMap[key] = value
    }

    fun put(key: String, value: String) {
        _dataMap[key] = value
    }

    fun put(key: String, value: ConfigurationSerializable) {
        _dataMap[key] = value
    }
}
