package com.dekitateserver.events.data

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.vo.LoginBonusId
import com.dekitateserver.events.infrastructure.source.LoginBonusHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player

class LoginBonusHistoryRepository(plugin: DekitateEventsPlugin) {

    private val loginBonusHistoryDao = LoginBonusHistoryDao(plugin.dataSource)

    suspend fun add(player: Player, loginBonusId: LoginBonusId): Boolean = withContext(Dispatchers.IO) {
        loginBonusHistoryDao.insert(player.uniqueId, loginBonusId)
    }

    suspend fun has(player: Player, loginBonusId: LoginBonusId): Boolean = withContext(Dispatchers.IO) {
        loginBonusHistoryDao.exists(player.uniqueId, loginBonusId)
    }
}
