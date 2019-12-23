package com.dekitateserver.events.data

import com.dekitateserver.events.data.entity.Key
import com.dekitateserver.events.data.source.KeyYamlSource
import com.dekitateserver.events.data.vo.KeyId
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class KeyRepository(plugin: JavaPlugin) {

    private val keyYamlSource = KeyYamlSource(plugin.dataFolder)

    private val keyCacheMap: MutableMap<KeyId, Key> = ConcurrentHashMap()

    init {
        createCache()
    }

    fun get(keyId: KeyId): Key? = keyCacheMap[keyId]

    fun getAll(): List<Key> = keyCacheMap.values.toList()

    fun getOrError(keyId: KeyId): Key? = keyCacheMap[keyId] ?: let {
        Log.error("Key(${keyId.value})が見つかりません.")
        return@let null
    }

    fun has(keyId: KeyId): Boolean = keyCacheMap.containsKey(keyId)

    suspend fun add(key: Key): Boolean = withContext(Dispatchers.IO) {
        if (keyCacheMap.containsKey(key.id)) {
            return@withContext false
        }

        return@withContext if (keyYamlSource.set(key)) {
            keyCacheMap[key.id] = key
            true
        } else {
            false
        }
    }

    suspend fun update(key: Key): Boolean = withContext(Dispatchers.IO) {
        if (!keyCacheMap.containsKey(key.id)) {
            return@withContext false
        }

        return@withContext if (keyYamlSource.set(key)) {
            keyCacheMap[key.id] = key
            true
        } else {
            false
        }
    }

    suspend fun remove(keyId: KeyId): Boolean = withContext(Dispatchers.IO) {
        if (!keyCacheMap.containsKey(keyId)) {
            return@withContext false
        }

        return@withContext if (keyYamlSource.delete(keyId)) {
            keyCacheMap.remove(keyId)
            true
        } else {
            false
        }
    }

    suspend fun refreshCache() = withContext(Dispatchers.IO) {
        keyCacheMap.clear()
        createCache()
    }

    private fun createCache() {
        keyCacheMap.putAll(
                keyYamlSource.getAll().associateBy { it.id }
        )
        Log.info("${keyCacheMap.size}個のKeyを読み込みました.")
    }
}
