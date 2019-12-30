package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.domain.entity.Parkour
import com.dekitateserver.events.domain.repository.ParkourRepository
import com.dekitateserver.events.domain.vo.ParkourId
import com.dekitateserver.events.infrastructure.source.ParkourYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class ParkourRepositoryImpl(plugin: JavaPlugin) : ParkourRepository {

    private val parkourYamlSource = ParkourYamlSource(plugin.dataFolder)

    private val parkourCacheMap: MutableMap<ParkourId, Parkour> = ConcurrentHashMap()

    init {
        createCache()
    }

    override fun has(parkourId: ParkourId): Boolean = parkourCacheMap.containsKey(parkourId)

    override fun get(parkourId: ParkourId): Parkour? = parkourCacheMap[parkourId]

    override fun getAll(): List<Parkour> = parkourCacheMap.values.toList()

    override fun getOrError(parkourId: ParkourId): Parkour? {
        val parkour = parkourCacheMap[parkourId]
        if (parkour == null) {
            Log.error("Parkour(${parkourId.value})が見つかりません")
        }

        return parkour
    }

    override suspend fun add(parkour: Parkour): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        parkourCacheMap.compute(parkour.id) { _, oldParkour: Parkour? ->
            if (oldParkour != null) {
                return@compute oldParkour
            }

            return@compute if (parkourYamlSource.set(parkour)) {
                isSuccessful = true
                parkour
            } else null
        }

        return@withContext isSuccessful
    }

    override suspend fun update(parkour: Parkour): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        parkourCacheMap.compute(parkour.id) { _, oldParkour: Parkour? ->
            if (oldParkour == null) {
                return@compute null
            }

            return@compute if (parkourYamlSource.set(parkour)) {
                isSuccessful = true
                parkour
            } else oldParkour
        }

        return@withContext isSuccessful
    }

    override suspend fun remove(parkourId: ParkourId): Boolean = withContext(Dispatchers.IO) {
        var isSuccessful = false

        parkourCacheMap.compute(parkourId) { _, oldParkour: Parkour? ->
            if (oldParkour == null) {
                return@compute null
            }

            return@compute if (parkourYamlSource.delete(parkourId)) {
                isSuccessful = true
                null
            } else oldParkour
        }

        return@withContext isSuccessful
    }

    override suspend fun refreshCache() = withContext(Dispatchers.IO) {
        parkourCacheMap.clear()
        createCache()
    }

    private fun createCache() {
        parkourCacheMap.putAll(
                parkourYamlSource.getAll().associateBy { it.id }
        )
        Log.info("${parkourCacheMap.size}個のParkourを読み込みました")
    }
}
