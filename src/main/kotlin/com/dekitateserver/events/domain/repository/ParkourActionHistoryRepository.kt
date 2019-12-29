package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourId
import org.bukkit.entity.Player
import java.time.LocalDateTime

interface ParkourActionHistoryRepository {

    suspend fun getLatestActionedDateTime(
            player: Player,
            parkourId: ParkourId,
            action: ParkourAction
    ): LocalDateTime?

    suspend fun add(
            player: Player,
            parkourId: ParkourId,
            action: ParkourAction,
            actionedDateTime: LocalDateTime
    ): Boolean
}
