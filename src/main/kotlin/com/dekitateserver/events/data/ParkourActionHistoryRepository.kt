package com.dekitateserver.events.data

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.source.ParkourActionHistoryDao
import com.dekitateserver.events.data.vo.ParkourAction
import com.dekitateserver.events.data.vo.ParkourId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.sql.Timestamp
import java.time.LocalDateTime

class ParkourActionHistoryRepository(plugin: DekitateEventsPlugin) {

    private val parkourActionHistoryDao = ParkourActionHistoryDao(plugin.dataSource)

    suspend fun add(
            player: Player,
            parkourId: ParkourId,
            action: ParkourAction,
            actionedDateTime: LocalDateTime
    ): Boolean = withContext(Dispatchers.IO) {
        parkourActionHistoryDao.insert(
                uuid = player.uniqueId,
                parkourId = parkourId,
                action = action,
                actionedDate = Timestamp.valueOf(actionedDateTime)
        )
    }
}
