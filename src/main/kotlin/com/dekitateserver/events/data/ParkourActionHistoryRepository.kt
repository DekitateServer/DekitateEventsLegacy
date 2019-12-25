package com.dekitateserver.events.data

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.source.ParkourActionHistoryDao
import com.dekitateserver.events.data.vo.ParkourAction
import com.dekitateserver.events.data.vo.ParkourId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.time.LocalDateTime

class ParkourActionHistoryRepository(plugin: DekitateEventsPlugin) {

    private val parkourActionHistoryDao = ParkourActionHistoryDao(plugin.dataSource)

    suspend fun getLatestActionedDateTime(
            player: Player,
            parkourId: ParkourId,
            action: ParkourAction
    ): LocalDateTime? = withContext(Dispatchers.IO) {
        parkourActionHistoryDao.selectLatestActionedDate(
                uuid = player.uniqueId,
                parkourId = parkourId,
                action = action
        )
    }

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
                actionedDate = actionedDateTime
        )
    }
}
