package com.dekitateserver.events.data

import com.dekitateserver.events.data.entity.SignMeta
import com.dekitateserver.events.data.source.SignMetaYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class SignMetaRepository(plugin: JavaPlugin) {

    private val signMetaYamlSource = SignMetaYamlSource(plugin.dataFolder)

    private val signMetaCacheMap: MutableMap<Location, SignMeta> = ConcurrentHashMap()

    init {
        createCache()
    }

    fun get(location: Location): SignMeta? = signMetaCacheMap[location]

    fun getOrNew(location: Location): SignMeta = signMetaCacheMap.getOrDefault(location, SignMeta(location))

    fun getOrError(location: Location): SignMeta? = signMetaCacheMap[location] ?: let {
        Log.error("SignMeta($location)が見つかりません.")
        return@let null
    }

    suspend fun add(signMeta: SignMeta): Boolean = withContext(Dispatchers.IO) {
        val isSuccessful = signMetaYamlSource.save(signMeta)
        if (isSuccessful) {
            signMetaCacheMap[signMeta.location] = signMeta
        }

        return@withContext isSuccessful
    }

    private fun createCache() {
        val signMetaMap = signMetaYamlSource.getAll().associateBy { it.location }
        signMetaCacheMap.putAll(signMetaMap)

        Log.info("${signMetaCacheMap.size}個のSignMetaを読み込みました.")
    }
}
