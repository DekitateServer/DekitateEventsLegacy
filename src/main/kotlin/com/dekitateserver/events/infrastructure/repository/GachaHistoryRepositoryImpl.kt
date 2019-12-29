package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.repository.GachaHistoryRepository
import com.dekitateserver.events.domain.vo.GachaId
import com.dekitateserver.events.infrastructure.source.GachaHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime

class GachaHistoryRepositoryImpl(plugin: DekitateEventsPlugin) : GachaHistoryRepository {

    private val gachaHistoryDao = GachaHistoryDao(plugin.dataSource)

    override suspend fun hasToday(gachaId: GachaId, gachaItemId: String): Boolean = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()

        return@withContext gachaHistoryDao.exists(
                gachaId = gachaId,
                gachaItemId = gachaItemId,
                start = Timestamp.valueOf(now.with(LocalTime.MIN)),
                end = Timestamp.valueOf(now.with(LocalTime.MAX))
        )
    }

    override suspend fun add(player: Player, gachaId: GachaId, gachaItemId: String): Boolean = withContext(Dispatchers.IO) {
        gachaHistoryDao.insert(player.uniqueId, gachaId, gachaItemId)
    }
}
