package com.dekitateserver.events.data

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.source.EventTicketHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime

class EventTicketHistoryRepository(plugin: DekitateEventsPlugin) {

    private val eventTicketHistoryDao = EventTicketHistoryDao(plugin.dataSource)

    suspend fun add(player: Player, amount: Int): Boolean = withContext(Dispatchers.IO) {
        eventTicketHistoryDao.insert(player.uniqueId, amount)
    }

    suspend fun getGaveAmountToday(player: Player): Int = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()

        return@withContext eventTicketHistoryDao.getPositiveAmountBetween(
                uuid = player.uniqueId,
                start = Timestamp.valueOf(now.with(LocalTime.MIN)),
                end = Timestamp.valueOf(now.with(LocalTime.MAX))
        )
    }
}
