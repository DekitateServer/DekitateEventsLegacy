package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.vo.LoginBonusId
import org.bukkit.entity.Player

interface LoginBonusHistoryRepository {

    suspend fun has(player: Player, loginBonusId: LoginBonusId): Boolean

    suspend fun add(player: Player, loginBonusId: LoginBonusId): Boolean
}
