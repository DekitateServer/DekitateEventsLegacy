package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.entity.Parkour
import com.dekitateserver.events.domain.vo.ParkourId

interface ParkourRepository {

    fun has(parkourId: ParkourId): Boolean

    fun get(parkourId: ParkourId): Parkour?

    fun getAll(): List<Parkour>

    fun getOrError(parkourId: ParkourId): Parkour?

    suspend fun add(parkour: Parkour): Boolean

    suspend fun update(parkour: Parkour): Boolean

    suspend fun remove(parkourId: ParkourId): Boolean

    suspend fun refreshCache()
}
