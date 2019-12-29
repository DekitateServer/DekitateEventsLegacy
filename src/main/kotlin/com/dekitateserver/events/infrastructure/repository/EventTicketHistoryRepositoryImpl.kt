package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.repository.EventTicketHistoryRepository
import com.dekitateserver.events.infrastructure.source.EventTicketHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime

class EventTicketHistoryRepositoryImpl(plugin: DekitateEventsPlugin) : EventTicketHistoryRepository {

    private val eventTicketHistoryDao = EventTicketHistoryDao(plugin.dataSource)

    override suspend fun getGaveAmountToday(player: Player): Int = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()

        return@withContext eventTicketHistoryDao.getPositiveAmountBetween(
                uuid = player.uniqueId,
                start = Timestamp.valueOf(now.with(LocalTime.MIN)),
                end = Timestamp.valueOf(now.with(LocalTime.MAX))
        )
    }

    override suspend fun add(player: Player, amount: Int): Boolean = withContext(Dispatchers.IO) {
        eventTicketHistoryDao.insert(player.uniqueId, amount)
    }
}
