package com.dekitateserver.events.data

import com.dekitateserver.events.data.entity.Password
import com.dekitateserver.events.data.source.PasswordYamlSource
import com.dekitateserver.events.data.vo.PasswordId
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class PasswordRepository(plugin: JavaPlugin) {

    private val passwordYamlSource = PasswordYamlSource(plugin.dataFolder)

    private val passwordCacheMap: MutableMap<PasswordId, Password> = ConcurrentHashMap()

    init {
        createCache()
    }

    fun get(passwordId: PasswordId): Password? = passwordCacheMap[passwordId]

    fun getAll(): List<Password> = passwordCacheMap.values.toList()

    fun getOrError(passwordId: PasswordId): Password? = passwordCacheMap[passwordId] ?: let {
        Log.error("Password(${passwordId.value})が見つかりません.")
        return@let null
    }

    fun has(passwordId: PasswordId): Boolean = passwordCacheMap.containsKey(passwordId)

    suspend fun add(password: Password): Boolean = withContext(Dispatchers.IO) {
        if (passwordCacheMap.containsKey(password.id)) {
            return@withContext false
        }

        return@withContext if (passwordYamlSource.set(password)) {
            passwordCacheMap[password.id] = password
            true
        } else {
            false
        }
    }

    suspend fun update(password: Password): Boolean = withContext(Dispatchers.IO) {
        if (!passwordCacheMap.containsKey(password.id)) {
            return@withContext false
        }

        return@withContext if (passwordYamlSource.set(password)) {
            passwordCacheMap[password.id] = password
            true
        } else {
            false
        }
    }

    suspend fun refreshCache() = withContext(Dispatchers.IO) {
        passwordCacheMap.clear()
        createCache()
    }

    private fun createCache() {
        passwordCacheMap.putAll(
                passwordYamlSource.getAll().associateBy { it.id }
        )
        Log.info("${passwordCacheMap.size}個のPasswordを読み込みました.")
    }
}
