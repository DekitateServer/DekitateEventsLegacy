package com.dekitateserver.events.data.source

import com.dekitateserver.core.data.source.YamlStorage
import com.dekitateserver.events.data.entity.Parkour
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import java.io.File

class ParkourYamlSource(dataFolder: File) {

    private val storage = YamlStorage(File(dataFolder, "parkour.yml"))

    fun getAll(): List<Parkour> {
        val parkourList = mutableListOf<Parkour>()

        try {
            val config = storage.loadYamlConfiguration()

            val parkourIdSet = config.root?.getKeys(false).orEmpty()

            val list = parkourIdSet.mapNotNull { id ->
                val name = config.getString("$id.name")
                if (name == null) {
                    Log.warn("Parkour($id)のnameがありません.")
                    return@mapNotNull null
                }

                return@mapNotNull Parkour(
                        id = ParkourId(id),
                        name = name,
                        rewardEventTicketAmount = config.getInt("$id.Reward.eventTicketAmount"),
                        joinLocation = config.getSerializable("$id.Join.Location", Location::class.java),
                        joinMessage = config.getString("$id.Join.message"),
                        joinBroadcastMessage = config.getString("$id.Join.broadcastMessage"),
                        clearLocation = config.getSerializable("$id.Clear.Location", Location::class.java),
                        clearMessage = config.getString("$id.Clear.message"),
                        clearBroadcastMessage = config.getString("$id.Clear.broadcastMessage"),
                        exitLocation = config.getSerializable("$id.Exit.Location", Location::class.java),
                        exitMessage = config.getString("$id.Exit.message")
                )
            }

            parkourList.addAll(list)
        } catch (e: Exception) {
            Log.error("Parkourの読み込みに失敗しました.", e)
        }

        return parkourList
    }

    fun set(parkour: Parkour): Boolean {
        val dataMap = linkedMapOf<String, Any?>().apply {
            val path = parkour.id.value

            put("$path.name", parkour.name)

            put("$path.Reward.eventTicketAmount", parkour.rewardEventTicketAmount)

            put("$path.Join.Location", parkour.joinLocation)
            put("$path.Join.message", parkour.joinMessage)
            put("$path.Join.broadcastMessage", parkour.joinBroadcastMessage)

            put("$path.Clear.Location", parkour.clearLocation)
            put("$path.Clear.message", parkour.clearMessage)
            put("$path.Clear.broadcastMessage", parkour.clearBroadcastMessage)

            put("$path.Exit.Location", parkour.exitLocation)
            put("$path.Exit.message", parkour.exitMessage)
        }

        return storage.save(dataMap)
    }

    fun delete(parkourId: ParkourId) = storage.save(mapOf(parkourId.value to null))
}
