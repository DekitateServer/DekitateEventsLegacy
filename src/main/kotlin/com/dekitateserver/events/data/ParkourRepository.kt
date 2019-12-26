package com.dekitateserver.events.data

import com.dekitateserver.events.data.entity.Parkour
import com.dekitateserver.events.data.source.ParkourYamlSource
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class ParkourRepository(plugin: JavaPlugin) {

    private val parkourYamlSource = ParkourYamlSource(plugin.dataFolder)

    private val parkourCacheMap: MutableMap<ParkourId, Parkour> = ConcurrentHashMap()

    init {
        createCache()
    }

    fun get(parkourId: ParkourId): Parkour? = parkourCacheMap[parkourId]

    fun getAll(): List<Parkour> = parkourCacheMap.values.toList()

    fun getOrError(parkourId: ParkourId): Parkour? = parkourCacheMap[parkourId] ?: let {
        Log.error("Parkour(${parkourId.value})が見つかりません")
        return@let null
    }

    fun has(parkourId: ParkourId): Boolean = parkourCacheMap.containsKey(parkourId)

    suspend fun add(parkour: Parkour): Boolean = withContext(Dispatchers.IO) {
        if (parkourCacheMap.containsKey(parkour.id)) {
            return@withContext false
        }

        return@withContext if (parkourYamlSource.set(parkour)) {
            parkourCacheMap[parkour.id] = parkour
            true
        } else {
            false
        }
    }

    suspend fun update(parkour: Parkour): Boolean = withContext(Dispatchers.IO) {
        if (!parkourCacheMap.containsKey(parkour.id)) {
            return@withContext false
        }

        return@withContext if (parkourYamlSource.set(parkour)) {
            parkourCacheMap[parkour.id] = parkour
            true
        } else {
            false
        }
    }

    suspend fun remove(parkourId: ParkourId): Boolean = withContext(Dispatchers.IO) {
        if (!parkourCacheMap.containsKey(parkourId)) {
            return@withContext false
        }

        return@withContext if (parkourYamlSource.delete(parkourId)) {
            parkourCacheMap.remove(parkourId)
            true
        } else {
            false
        }
    }

    suspend fun refreshCache() = withContext(Dispatchers.IO) {
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
