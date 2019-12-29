package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.vo.DungeonAction
import com.dekitateserver.events.domain.vo.DungeonId
import org.bukkit.entity.Player
import java.time.LocalDateTime

interface DungeonActionHistoryRepository {

    suspend fun getLatestActionedDateTime(
            player: Player,
            dungeonId: DungeonId,
            action: DungeonAction
    ): LocalDateTime?

    suspend fun add(
            player: Player,
            dungeonId: DungeonId,
            action: DungeonAction,
            actionedDateTime: LocalDateTime
    ): Boolean
}
