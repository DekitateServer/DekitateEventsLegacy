package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.entity.Dungeon
import com.dekitateserver.events.domain.vo.DungeonId

interface DungeonRepository {

    fun has(dungeonId: DungeonId): Boolean

    fun get(dungeonId: DungeonId): Dungeon?

    fun getAll(): List<Dungeon>

    fun getOrError(dungeonId: DungeonId): Dungeon?

    suspend fun add(dungeon: Dungeon): Boolean

    suspend fun update(dungeon: Dungeon): Boolean

    suspend fun remove(dungeonId: DungeonId): Boolean

    suspend fun lock(dungeonId: DungeonId, seconds: Long): Boolean

    suspend fun unlock(dungeonId: DungeonId): Boolean

    suspend fun refreshCache()
}
