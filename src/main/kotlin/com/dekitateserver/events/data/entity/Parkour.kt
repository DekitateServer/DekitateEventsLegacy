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
        val startMessage: String? = "§aスタート時間を記録しました.",
        val endMessage: String? = "{name}§r§bを §a{time} §bでクリア§r!",
        val endBroadcastMessage: String? = null,
        val exitLocation: Location? = null,
        val exitMessage: String? = null
) {
    val formattedJoinMessage = joinMessage?.format()
    val formattedJoinBroadcastMessage = joinBroadcastMessage?.format()
    val formattedStartMessage = startMessage?.format()
    val formattedEndMessage = endMessage?.format()
    val formattedEndBroadcastMessage = endBroadcastMessage?.format()
    val formattedExitMessage = exitMessage?.format()

    private fun String.format() = replace("{name}", name)
}
