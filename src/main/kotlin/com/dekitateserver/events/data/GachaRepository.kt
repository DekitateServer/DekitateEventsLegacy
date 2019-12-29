package com.dekitateserver.events.data

import com.dekitateserver.events.data.entity.Gacha
import com.dekitateserver.events.domain.vo.GachaId
import com.dekitateserver.events.infrastructure.source.GachaYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class GachaRepository(plugin: JavaPlugin) {

    private val gachaYamlSource = GachaYamlSource(plugin.dataFolder)

    private val gachaCacheMap: MutableMap<GachaId, Gacha> = ConcurrentHashMap()

    init {
        createCache()
    }

    fun get(gachaId: GachaId): Gacha? = gachaCacheMap[gachaId]

    fun getAll(): List<Gacha> = gachaCacheMap.values.toList()

    fun getOrError(gachaId: GachaId): Gacha? = gachaCacheMap[gachaId] ?: let {
        Log.error("Gacha(${gachaId.value})が見つかりません")
        return@let null
    }

    suspend fun refreshCache() = withContext(Dispatchers.IO) {
        gachaCacheMap.clear()
        createCache()
    }

    private fun createCache() {
        gachaCacheMap.putAll(
                gachaYamlSource.getAll().associateBy { it.id }
        )
        Log.info("${gachaCacheMap.size}個のGachaを読み込みました")
    }
}
