package com.dekitateserver.events.data.entity

import com.dekitateserver.events.data.vo.ParkourId
import org.bukkit.Location

data class Parkour(
        val id: ParkourId,
        val name: String,
        val rewardEventTicketAmount: Int = 0,
        val joinLocation: Location? = null,
        val joinMessage: String? = "{name}§rに§c挑戦§r!",
        val joinBroadcastMessage: String? = null,
        val clearLocation: Location? = null,
        val clearMessage: String? = "{name}§rを§bクリア§r!",
        val clearBroadcastMessage: String? = null,
        val exitLocation: Location? = null,
        val exitMessage: String? = null
) {
    val hasRewardEventTicket = rewardEventTicketAmount > 0

    val formattedJoinMessage = joinMessage?.format()
    val formattedJoinBroadcastMessage = joinBroadcastMessage?.format()
    val formattedClearMessage = clearMessage?.format()
    val formattedClearBroadcastMessage = clearBroadcastMessage?.format()
    val formattedExitMessage = exitMessage?.format()

    private fun String.format() = replace("{name}", name)
}
