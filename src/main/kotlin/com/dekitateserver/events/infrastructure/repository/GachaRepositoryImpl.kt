package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.domain.entity.Gacha
import com.dekitateserver.events.domain.repository.GachaRepository
import com.dekitateserver.events.domain.vo.GachaId
import com.dekitateserver.events.infrastructure.source.GachaYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class GachaRepositoryImpl(plugin: JavaPlugin) : GachaRepository {

    private val gachaYamlSource = GachaYamlSource(plugin.dataFolder)

    private val gachaCacheMap: MutableMap<GachaId, Gacha> = ConcurrentHashMap()

    init {
        createCache()
    }

    override fun get(gachaId: GachaId): Gacha? = gachaCacheMap[gachaId]

    override fun getAll(): List<Gacha> = gachaCacheMap.values.toList()

    override fun getOrError(gachaId: GachaId): Gacha? {
        val gacha = gachaCacheMap[gachaId]
        if (gacha == null) {
            Log.error("Gacha(${gachaId.value})が見つかりません")
        }

        return gacha
    }

    override suspend fun refreshCache() = withContext(Dispatchers.IO) {
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
