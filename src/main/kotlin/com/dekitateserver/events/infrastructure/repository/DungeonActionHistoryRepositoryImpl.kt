package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.repository.DungeonActionHistoryRepository
import com.dekitateserver.events.domain.vo.DungeonAction
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.infrastructure.source.DungeonActionHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.time.LocalDateTime

class DungeonActionHistoryRepositoryImpl(plugin: DekitateEventsPlugin) : DungeonActionHistoryRepository {

    private val dungeonActionHistoryDao = DungeonActionHistoryDao(plugin.dataSource)

    override suspend fun getLatestActionedDateTime(
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

    override suspend fun add(
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
