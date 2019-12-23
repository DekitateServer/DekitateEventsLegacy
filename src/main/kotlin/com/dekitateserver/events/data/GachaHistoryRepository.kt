package com.dekitateserver.events.data

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.source.GachaHistoryDao
import com.dekitateserver.events.data.vo.GachaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime

class GachaHistoryRepository(plugin: DekitateEventsPlugin) {

    private val gachaHistoryDao = GachaHistoryDao(plugin.dataSource)

    suspend fun add(player: Player, gachaId: GachaId, gachaItemId: String): Boolean = withContext(Dispatchers.IO) {
        gachaHistoryDao.insert(player.uniqueId, gachaId, gachaItemId)
    }

    suspend fun hasToday(gachaId: GachaId, gachaItemId: String): Boolean = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()

        return@withContext gachaHistoryDao.exists(
                gachaId = gachaId,
                gachaItemId = gachaItemId,
                start = Timestamp.valueOf(now.with(LocalTime.MIN)),
                end = Timestamp.valueOf(now.with(LocalTime.MAX))
        )
    }
}
