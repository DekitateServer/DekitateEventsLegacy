package com.dekitateserver.events.data.vo

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.source.DungeonActionHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.time.LocalDateTime

class DungeonActionHistoryRepository(plugin: DekitateEventsPlugin) {

    private val dungeonActionHistoryDao = DungeonActionHistoryDao(plugin.dataSource)

    suspend fun getLatestActionedDateTime(
            player: Player,
            dungeonId: DungeonId,
            action: DungeonAction
    ): LocalDateTime? = withContext(Dispatchers.IO) {
        dungeonActionHistoryDao.selectLatestActionedDate(
                uuid = player.uniqueId,
                dungeonId = dungeonId,
                action = action
        )
    }

    suspend fun add(
            player: Player,
            dungeonId: DungeonId,
            action: DungeonAction,
            actionedDateTime: LocalDateTime
    ): Boolean = withContext(Dispatchers.IO) {
        dungeonActionHistoryDao.insert(
                uuid = player.uniqueId,
                dungeonId = dungeonId,
                action = action,
                actionedDate = actionedDateTime
        )
    }
}
