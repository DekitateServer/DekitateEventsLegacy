package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.domain.entity.SignMeta
import com.dekitateserver.events.domain.repository.SignMetaRepository
import com.dekitateserver.events.infrastructure.source.SignMetaYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class SignMetaRepositoryImpl(plugin: JavaPlugin) : SignMetaRepository {

    private val signMetaYamlSource = SignMetaYamlSource(plugin.dataFolder)

    private val signMetaCacheMap: MutableMap<Location, SignMeta> = ConcurrentHashMap()

    init {
        createCache()
    }

    override fun get(location: Location): SignMeta? = signMetaCacheMap[location]

    override fun getOrNew(location: Location): SignMeta = signMetaCacheMap.getOrDefault(location, SignMeta(location))

    override fun getOrError(location: Location): SignMeta? = signMetaCacheMap[location] ?: let {
        Log.error("SignMeta($location)が見つかりません")
        return@let null
    }

    override suspend fun add(signMeta: SignMeta): Boolean = withContext(Dispatchers.IO) {
        val isSuccessful = signMetaYamlSource.save(signMeta)
        if (isSuccessful) {
            signMetaCacheMap[signMeta.location] = signMeta
        }

        return@withContext isSuccessful
    }

    private fun createCache() {
        val signMetaMap = signMetaYamlSource.getAll().associateBy { it.location }
        signMetaCacheMap.putAll(signMetaMap)

        Log.info("${signMetaCacheMap.size}個のSignMetaを読み込みました")
    }
}
