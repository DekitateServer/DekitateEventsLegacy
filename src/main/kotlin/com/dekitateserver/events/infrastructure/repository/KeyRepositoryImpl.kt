package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.domain.entity.Key
import com.dekitateserver.events.domain.repository.KeyRepository
import com.dekitateserver.events.domain.vo.KeyId
import com.dekitateserver.events.infrastructure.source.KeyYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class KeyRepositoryImpl(plugin: JavaPlugin) : KeyRepository {

    private val keyYamlSource = KeyYamlSource(plugin.dataFolder)

    private val keyCacheMap: MutableMap<KeyId, Key> = ConcurrentHashMap()

    init {
        createCache()
    }

    override fun has(keyId: KeyId): Boolean = keyCacheMap.containsKey(keyId)

    override fun get(keyId: KeyId): Key? = keyCacheMap[keyId]

    override fun getAll(): List<Key> = keyCacheMap.values.toList()

    override fun getOrError(keyId: KeyId): Key? {
        val key = keyCacheMap[keyId]
        if (key == null) {
            Log.error("Key(${keyId.value})が見つかりません")
        }

        return key
    }

    override suspend fun add(key: Key): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        keyCacheMap.compute(key.id) { _, oldKey: Key? ->
            if (oldKey != null) {
                return@compute oldKey
            }

            return@compute if (keyYamlSource.set(key)) {
                isSuccessful = true
                key
            } else null
        }

        return@withContext isSuccessful
    }

    override suspend fun update(key: Key): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        keyCacheMap.compute(key.id) { _, oldKey: Key? ->
            if (oldKey == null) {
                return@compute null
            }

            return@compute if (keyYamlSource.set(key)) {
                isSuccessful = true
                key
            } else oldKey
        }

        return@withContext isSuccessful
    }

    override suspend fun remove(keyId: KeyId): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        keyCacheMap.compute(keyId) { _, oldKey: Key? ->
            if (oldKey == null) {
                return@compute null
            }

            return@compute if (keyYamlSource.delete(keyId)) {
                isSuccessful = true
                null
            } else oldKey
        }

        return@withContext isSuccessful
    }

    override suspend fun refreshCache() = withContext(Dispatchers.IO) {
        keyCacheMap.clear()
        createCache()
    }

    private fun createCache() {
        keyCacheMap.putAll(
                keyYamlSource.getAll().associateBy { it.id }
        )
        Log.info("${keyCacheMap.size}個のKeyを読み込みました")
    }
}
