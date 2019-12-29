package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.domain.entity.Dungeon
import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.infrastructure.source.DungeonYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class DungeonRepositoryImpl(plugin: JavaPlugin) : DungeonRepository {

    private val dungeonYamlSource = DungeonYamlSource(plugin.dataFolder)

    private val dungeonCacheMap: MutableMap<DungeonId, Dungeon> = ConcurrentHashMap()

    init {
        createCache()
    }

    override fun has(dungeonId: DungeonId): Boolean = dungeonCacheMap.containsKey(dungeonId)

    override fun get(dungeonId: DungeonId): Dungeon? = dungeonCacheMap[dungeonId]

    override fun getAll(): List<Dungeon> = dungeonCacheMap.values.toList()

    override fun getOrError(dungeonId: DungeonId): Dungeon? {
        val dungeon = dungeonCacheMap[dungeonId]
        if (dungeon == null) {
            Log.error("Dungeon(${dungeonId.value})が見つかりません")
        }

        return dungeon
    }

    override suspend fun add(dungeon: Dungeon): Boolean = withContext(Dispatchers.IO) {
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

    override suspend fun update(dungeon: Dungeon): Boolean = withContext(Dispatchers.IO) {
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

    override suspend fun remove(dungeonId: DungeonId): Boolean = withContext(Dispatchers.IO) {
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

    override suspend fun lock(dungeonId: DungeonId, seconds: Long): Boolean {
        val dungeon = getOrError(dungeonId)?.copy(
                lockEndDateTime = LocalDateTime.now().plusSeconds(seconds)
        ) ?: return false

        return update(dungeon)
    }

    override suspend fun unlock(dungeonId: DungeonId): Boolean {
        val dungeon = getOrError(dungeonId)?.copy(lockEndDateTime = null) ?: return false
        return update(dungeon)
    }

    override suspend fun refreshCache() = withContext(Dispatchers.IO) {
        dungeonCacheMap.clear()
        createCache()
    }

    private fun createCache() {
        dungeonCacheMap.putAll(
                dungeonYamlSource.getAll().associateBy { it.id }
        )
        Log.info("${dungeonCacheMap.size}個のDungeonを読み込みました")
    }
}
