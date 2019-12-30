package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.domain.entity.Password
import com.dekitateserver.events.domain.repository.PasswordRepository
import com.dekitateserver.events.domain.vo.PasswordId
import com.dekitateserver.events.infrastructure.source.PasswordYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class PasswordRepositoryImpl(plugin: JavaPlugin) : PasswordRepository {

    private val passwordYamlSource = PasswordYamlSource(plugin.dataFolder)

    private val passwordCacheMap: MutableMap<PasswordId, Password> = ConcurrentHashMap()

    init {
        createCache()
    }

    override fun has(passwordId: PasswordId): Boolean = passwordCacheMap.containsKey(passwordId)

    override fun get(passwordId: PasswordId): Password? = passwordCacheMap[passwordId]

    override fun getAll(): List<Password> = passwordCacheMap.values.toList()

    override fun getOrError(passwordId: PasswordId): Password? {
        val password = passwordCacheMap[passwordId]
        if (password == null) {
            Log.error("Password(${passwordId.value})が見つかりません")
        }

        return password
    }

    override suspend fun add(password: Password): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        passwordCacheMap.compute(password.id) { _, oldPassword: Password? ->
            if (oldPassword != null) {
                return@compute oldPassword
            }

            return@compute if (passwordYamlSource.set(password)) {
                isSuccessful = true
                password
            } else null
        }

        return@withContext isSuccessful
    }

    override suspend fun update(password: Password): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        passwordCacheMap.compute(password.id) { _, oldPassword: Password? ->
            if (oldPassword == null) {
                return@compute null
            }

            return@compute if (passwordYamlSource.set(password)) {
                isSuccessful = true
                password
            } else oldPassword
        }

        return@withContext isSuccessful
    }

    override suspend fun remove(passwordId: PasswordId): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        passwordCacheMap.compute(passwordId) { _, oldPassword: Password? ->
            if (oldPassword == null) {
                return@compute null
            }

            return@compute if (passwordYamlSource.delete(passwordId)) {
                isSuccessful = true
                null
            } else oldPassword
        }

        return@withContext isSuccessful
    }

    override suspend fun refreshCache() = withContext(Dispatchers.IO) {
        passwordCacheMap.clear()
        createCache()
    }

    private fun createCache() {
        passwordCacheMap.putAll(
                passwordYamlSource.getAll().associateBy { it.id }
        )
        Log.info("${passwordCacheMap.size}個のPasswordを読み込みました")
    }
}
