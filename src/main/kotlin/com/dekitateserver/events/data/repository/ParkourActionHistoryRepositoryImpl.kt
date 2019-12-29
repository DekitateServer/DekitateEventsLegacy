package com.dekitateserver.events.data.repository

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.source.ParkourActionHistoryDao
import com.dekitateserver.events.domain.repository.ParkourActionHistoryRepository
import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.time.LocalDateTime

class ParkourActionHistoryRepositoryImpl(plugin: DekitateEventsPlugin) : ParkourActionHistoryRepository {

    private val parkourActionHistoryDao = ParkourActionHistoryDao(plugin.dataSource)

    override suspend fun getLatestActionedDateTime(
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

    override suspend fun add(
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
