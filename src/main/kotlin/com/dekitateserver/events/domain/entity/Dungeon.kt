package com.dekitateserver.events.domain.entity

import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.domain.vo.GachaId
import org.bukkit.Location
import java.time.LocalDateTime

data class Dungeon(
        val id: DungeonId,
        val name: String,
        val rewardEventTicketAmount: Int = 0,
        val rewardGachaId: GachaId? = null,
        val joinLocation: Location? = null,
        val joinMessage: String? = "{name}§rに§c参加§rしました!",
        val joinBroadcastMessage: String? = null,
        val completeLocation: Location? = null,
        val completeMessage: String? = "タイム §a\"{time}\" §rで§dクリア! §rまた§c挑戦§rしてね!",
        val completeBroadcastMessage: String? = null,
        val isEnabledCompleteSound: Boolean = true,
        val exitLocation: Location? = null,
        val exitMessage: String? = null,
        val lockEndDateTime: LocalDateTime? = null,
        val lockMessage: String? = "{name}§rは§cロック中§rです! §b{time}§rお待ちください...",
        val lockBroadcastMessage: String? = null,
        val unlockBroadcastMessage: String? = null
) {
    val formattedJoinMessage = joinMessage?.format()
    val formattedJoinBroadcastMessage = joinBroadcastMessage?.format()
    val formattedCompleteMessage = completeMessage?.format()
    val formattedCompleteBroadcastMessage = completeBroadcastMessage?.format()
    val formattedExitMessage = exitMessage?.format()
    val formattedLockMessage = lockMessage?.format()
    val formattedLockBroadcastMessage = lockBroadcastMessage?.format()
    val formattedUnlockBroadcastMessage = unlockBroadcastMessage?.format()

    private fun String.format() = replace("{name}", name)
}
