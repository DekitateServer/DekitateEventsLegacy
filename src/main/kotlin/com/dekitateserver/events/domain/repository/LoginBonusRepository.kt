package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.entity.LoginBonus
import com.dekitateserver.events.domain.vo.LoginBonusId

interface LoginBonusRepository {

    fun get(loginBonusId: LoginBonusId): LoginBonus?

    fun getListByNow(): List<LoginBonus>

    fun getAll(): List<LoginBonus>

    suspend fun refreshCache()
}
