package com.dekitateserver.events.domain.repository

import com.dekitateserver.events.domain.entity.SignMeta
import org.bukkit.Location

interface SignMetaRepository {

    fun get(location: Location): SignMeta?

    fun getOrNew(location: Location): SignMeta

    fun getOrError(location: Location): SignMeta?

    suspend fun add(signMeta: SignMeta): Boolean
}
