package com.dekitateserver.events.domain.repository

import org.bukkit.entity.Player

interface EventTicketHistoryRepository {

    suspend fun add(player: Player, amount: Int): Boolean

    suspend fun getGaveAmountToday(player: Player): Int
}
