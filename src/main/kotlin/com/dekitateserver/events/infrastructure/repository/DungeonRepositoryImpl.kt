package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.domain.entity.Dungeon
import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.event.DungeonLockTimedOutEvent
import com.dekitateserver.events.infrastructure.source.DungeonYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class DungeonRepositoryImpl(plugin: JavaPlugin) : DungeonRepository {

    // for schedule event
    private val plugin: Plugin = plugin
    private val pluginManager = plugin.server.pluginManager
    private val scheduler = plugin.server.scheduler

    private val dungeonYamlSource = DungeonYamlSource(plugin.dataFolder)

    private val dungeonCacheMap: MutableMap<DungeonId, Dungeon> = ConcurrentHashMap()

    private val dungeonLockTimedOutEventTaskMap: MutableMap<DungeonId, BukkitTask> = ConcurrentHashMap()

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
        var isSuccessful = false

        dungeonCacheMap.compute(dungeon.id) { _, oldDungeon: Dungeon? ->
            if (oldDungeon != null) {
                return@compute oldDungeon
            }

            if (!dungeonYamlSource.set(dungeon)) {
                return@compute null
            }

            isSuccessful = true

            return@compute dungeon
        }

        return@withContext isSuccessful
    }

    override suspend fun update(dungeon: Dungeon): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        dungeonCacheMap.compute(dungeon.id) { _, oldDungeon: Dungeon? ->
            if (oldDungeon == null) {
                return@compute null
            }

            if (!dungeonYamlSource.set(dungeon)) {
                return@compute oldDungeon
            }

            isSuccessful = true

            cancelDungeonLockTimedOutEventIfNeeded(dungeon)

            return@compute dungeon
        }

        return@withContext isSuccessful
    }

    override suspend fun remove(dungeonId: DungeonId): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        dungeonCacheMap.compute(dungeonId) { _, oldDungeon: Dungeon? ->
            if (oldDungeon == null) {
                return@compute null
            }

            if (!dungeonYamlSource.delete(dungeonId)) {
                return@compute oldDungeon
            }

            isSuccessful = true

            return@compute null
        }

        return@withContext isSuccessful
    }

    override suspend fun lock(dungeonId: DungeonId, seconds: Long): Boolean {
        val dungeon = getOrError(dungeonId)?.copy(
                lockEndDateTime = LocalDateTime.now().plusSeconds(seconds)
        ) ?: return false

        scheduleDungeonLockTimedOutEvent(dungeonId, seconds)

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

    private fun scheduleDungeonLockTimedOutEvent(dungeonId: DungeonId, seconds: Long) {
        dungeonLockTimedOutEventTaskMap.compute(dungeonId) { _, bukkitTask: BukkitTask? ->
            bukkitTask?.cancel()

            return@compute scheduler.runTaskLaterAsynchronously(plugin, Runnable {
                pluginManager.callEvent(DungeonLockTimedOutEvent(dungeonId))
            }, seconds * 20L)
        }
    }

    private fun cancelDungeonLockTimedOutEventIfNeeded(dungeon: Dungeon) {
        if (dungeon.isLocked) {
            return
        }

        dungeonLockTimedOutEventTaskMap.remove(dungeon.id)?.cancel()
    }
}
