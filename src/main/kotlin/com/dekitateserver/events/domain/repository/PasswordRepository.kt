package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.entity.Password
import com.dekitateserver.events.domain.vo.PasswordId

interface PasswordRepository {

    fun has(passwordId: PasswordId): Boolean

    fun get(passwordId: PasswordId): Password?

    fun getAll(): List<Password>

    fun getOrError(passwordId: PasswordId): Password?

    suspend fun add(password: Password): Boolean

    suspend fun update(password: Password): Boolean

    suspend fun remove(passwordId: PasswordId): Boolean

    suspend fun refreshCache()
}
