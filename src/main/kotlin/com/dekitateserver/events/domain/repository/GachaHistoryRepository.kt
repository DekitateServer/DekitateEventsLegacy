package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.vo.GachaId
import org.bukkit.entity.Player

interface GachaHistoryRepository {

    suspend fun hasToday(gachaId: GachaId, gachaItemId: String): Boolean

    suspend fun add(player: Player, gachaId: GachaId, gachaItemId: String): Boolean
}
