package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.repository.LoginBonusHistoryRepository
import com.dekitateserver.events.domain.vo.LoginBonusId
import com.dekitateserver.events.infrastructure.source.LoginBonusHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player

class LoginBonusHistoryRepositoryImpl(plugin: DekitateEventsPlugin) : LoginBonusHistoryRepository {

    private val loginBonusHistoryDao = LoginBonusHistoryDao(plugin.dataSource)

    override suspend fun has(player: Player, loginBonusId: LoginBonusId): Boolean = withContext(Dispatchers.IO) {
        loginBonusHistoryDao.exists(player.uniqueId, loginBonusId)
    }

    override suspend fun add(player: Player, loginBonusId: LoginBonusId): Boolean = withContext(Dispatchers.IO) {
        loginBonusHistoryDao.insert(player.uniqueId, loginBonusId)
    }
}
