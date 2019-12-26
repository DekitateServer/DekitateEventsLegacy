package com.dekitateserver.events.data.source

import com.dekitateserver.core.data.source.YamlStorage
import com.dekitateserver.events.data.entity.Dungeon
import com.dekitateserver.events.data.vo.DungeonId
import com.dekitateserver.events.data.vo.GachaId
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import java.io.File
import java.time.LocalDateTime

class DungeonYamlSource(dataFolder: File) {

    private val storage = YamlStorage(File(dataFolder, "dungeon.yml"))

    fun getAll(): List<Dungeon> {
        val dungeonList = mutableListOf<Dungeon>()

        try {
            val config = storage.loadYamlConfiguration()

            val dungeonIdSet = config.root?.getKeys(false).orEmpty()

            val list = dungeonIdSet.mapNotNull { id ->
                val name = config.getString("$id.name")
                if (name == null) {
                    Log.warn("Dungeon($id)のnameがありません")
                    return@mapNotNull null
                }

                return@mapNotNull Dungeon(
                        id = DungeonId(id),
                        name = name,
                        rewardEventTicketAmount = config.getInt("$id.Reward.eventTicketAmount"),
                        rewardGachaId = config.getString("$id.Reward.gachaId")?.let { GachaId(it) },
                        joinLocation = config.getSerializable("$id.Join.Location", Location::class.java),
                        joinMessage = config.getString("$id.Join.message"),
                        joinBroadcastMessage = config.getString("$id.Join.broadcastMessage"),
                        completeLocation = config.getSerializable("$id.Complete.Location", Location::class.java),
                        completeMessage = config.getString("$id.Complete.message"),
                        completeBroadcastMessage = config.getString("$id.Complete.broadcastMessage"),
                        isEnabledCompleteSound = config.getBoolean("$id.Complete.enabledSound"),
                        exitLocation = config.getSerializable("$id.Exit.Location", Location::class.java),
                        exitMessage = config.getString("$id.Exit.message"),
                        lockEndDateTime = config.getString("$id.Lock.endDateTime")?.let { LocalDateTime.parse(it) },
                        lockMessage = config.getString("$id.Lock.message"),
                        lockBroadcastMessage = config.getString("$id.Lock.broadcastMessage"),
                        unLockBroadcastMessage = config.getString("$id.UnLock.broadcastMessage")
                )
            }

            dungeonList.addAll(list)
        } catch (e: Exception) {
            Log.error("Dungeonの読み込みに失敗しました", e)
        }

        return dungeonList
    }

    fun set(dungeon: Dungeon): Boolean {
        val dataMap = linkedMapOf<String, Any?>().apply {
            val path = dungeon.id.value

            put("$path.name", dungeon.name)

            put("$path.Reward.eventTicketAmount", dungeon.rewardEventTicketAmount)
            put("$path.Reward.gachaId", dungeon.rewardGachaId)

            put("$path.Join.Location", dungeon.joinLocation)
            put("$path.Join.message", dungeon.joinMessage)
            put("$path.Join.broadcastMessage", dungeon.joinBroadcastMessage)

            put("$path.Complete.Location", dungeon.completeLocation)
            put("$path.Complete.message", dungeon.completeMessage)
            put("$path.Complete.broadcastMessage", dungeon.completeBroadcastMessage)
            put("$path.Complete.enabledSound", dungeon.isEnabledCompleteSound)

            put("$path.Exit.Location", dungeon.exitLocation)
            put("$path.Exit.message", dungeon.exitMessage)

            put("$path.Lock.endDateTime", dungeon.lockEndDateTime?.toString())
            put("$path.Lock.message", dungeon.lockMessage)
            put("$path.Lock.broadcastMessage", dungeon.lockBroadcastMessage)

            put("$path.UnLock.broadcastMessage", dungeon.unLockBroadcastMessage)
        }

        return storage.save(dataMap)
    }

    fun delete(dungeonId: DungeonId) = storage.save(mapOf(dungeonId.value to null))
}
