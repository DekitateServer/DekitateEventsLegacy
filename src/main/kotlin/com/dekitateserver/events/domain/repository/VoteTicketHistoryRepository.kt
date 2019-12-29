package com.dekitateserver.events.domain.repository

import org.bukkit.entity.Player

interface VoteTicketHistoryRepository {

    suspend fun getGaveAmountToday(player: Player): Int

    suspend fun add(player: Player, amount: Int): Boolean
}
