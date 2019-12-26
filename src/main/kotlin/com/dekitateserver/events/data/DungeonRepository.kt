package com.dekitateserver.events.data

import com.dekitateserver.events.data.entity.Dungeon
import com.dekitateserver.events.data.source.DungeonYamlSource
import com.dekitateserver.events.data.vo.DungeonId
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class DungeonRepository(plugin: JavaPlugin) {

    private val dungeonYamlSource = DungeonYamlSource(plugin.dataFolder)

    private val dungeonCacheMap: MutableMap<DungeonId, Dungeon> = ConcurrentHashMap()

    init {
        createCache()
    }

    fun get(dungeonId: DungeonId): Dungeon? = dungeonCacheMap[dungeonId]

    fun getAll(): List<Dungeon> = dungeonCacheMap.values.toList()

    fun getOrError(dungeonId: DungeonId): Dungeon? = dungeonCacheMap[dungeonId] ?: let {
        Log.error("Dungeon(${dungeonId.value})が見つかりません")
        return@let null
    }

    fun has(dungeonId: DungeonId): Boolean = dungeonCacheMap.containsKey(dungeonId)

    suspend fun add(dungeon: Dungeon): Boolean = withContext(Dispatchers.IO) {
        if (dungeonCacheMap.containsKey(dungeon.id)) {
            return@withContext false
        }

        return@withContext if (dungeonYamlSource.set(dungeon)) {
            dungeonCacheMap[dungeon.id] = dungeon
            true
        } else {
            false
        }
    }

    suspend fun update(dungeon: Dungeon): Boolean = withContext(Dispatchers.IO) {
        if (!dungeonCacheMap.containsKey(dungeon.id)) {
            return@withContext false
        }

        return@withContext if (dungeonYamlSource.set(dungeon)) {
            dungeonCacheMap[dungeon.id] = dungeon
            true
        } else {
            false
        }
    }

    suspend fun remove(dungeonId: DungeonId): Boolean = withContext(Dispatchers.IO) {
        if (!dungeonCacheMap.containsKey(dungeonId)) {
            return@withContext false
        }

        return@withContext if (dungeonYamlSource.delete(dungeonId)) {
            dungeonCacheMap.remove(dungeonId)
            true
        } else {
            false
        }
    }

    suspend fun lock(dungeonId: DungeonId, seconds: Long): Boolean {
        val dungeon = getOrError(dungeonId)?.copy(
                lockEndDateTime = LocalDateTime.now().plusSeconds(seconds)
        ) ?: return false

        return update(dungeon)
    }

    suspend fun refreshCache() = withContext(Dispatchers.IO) {
        dungeonCacheMap.clear()
        createCache()
    }

    private fun createCache() {
        dungeonCacheMap.putAll(
                dungeonYamlSource.getAll().associateBy { it.id }
        )
        Log.info("${dungeonCacheMap.size}個のParkourを読み込みました")
    }
}
