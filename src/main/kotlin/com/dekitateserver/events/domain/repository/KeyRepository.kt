package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.entity.Key
import com.dekitateserver.events.domain.vo.KeyId

interface KeyRepository {

    fun has(keyId: KeyId): Boolean

    fun get(keyId: KeyId): Key?

    fun getAll(): List<Key>

    fun getOrError(keyId: KeyId): Key?

    suspend fun add(key: Key): Boolean

    suspend fun update(key: Key): Boolean

    suspend fun remove(keyId: KeyId): Boolean

    suspend fun refreshCache()
}
