package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.entity.Gacha
import com.dekitateserver.events.domain.vo.GachaId

interface GachaRepository {

    fun get(gachaId: GachaId): Gacha?

    fun getAll(): List<Gacha>

    fun getOrError(gachaId: GachaId): Gacha?

    suspend fun refreshCache()
}
