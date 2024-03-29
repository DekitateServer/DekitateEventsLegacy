package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.repository.VoteTicketHistoryRepository
import com.dekitateserver.events.infrastructure.source.VoteTicketHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime

class VoteTicketHistoryRepositoryImpl(plugin: DekitateEventsPlugin) : VoteTicketHistoryRepository {

    private val voteTicketLogDao = VoteTicketHistoryDao(plugin.dataSource)

    override suspend fun getGaveAmountToday(player: Player): Int = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()

        return@withContext voteTicketLogDao.getPositiveAmountBetween(
                uuid = player.uniqueId,
                start = Timestamp.valueOf(now.with(LocalTime.MIN)),
                end = Timestamp.valueOf(now.with(LocalTime.MAX))
        )
    }

    override suspend fun add(player: Player, amount: Int): Boolean = withContext(Dispatchers.IO) {
        voteTicketLogDao.insert(player.uniqueId, amount)
    }
}
