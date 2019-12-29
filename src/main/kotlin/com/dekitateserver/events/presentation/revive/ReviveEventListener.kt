package com.dekitateserver.events.presentation.revive

import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

class ReviveEventListener(
        private val reviveController: ReviveController
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val blockState = event.clickedBlock?.state

        if (blockState is Sign && event.action == Action.RIGHT_CLICK_BLOCK && blockState.getLine(0) == ReviveSignContracts.SIGN_INDEX) {
            reviveController.clickSign(event.player, blockState.location)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onSignChanged(event: SignChangeEvent) {
        if (event.getLine(0) == ReviveSignContracts.CREATE_SIGN_INDEX && event.player.hasPermission("neko.event")) {
            reviveController.createSign(
                    player = event.player,
                    location = event.block.location,
                    argLocation = event.getLine(1).orEmpty(),
                    arcExperience = event.getLine(2).orEmpty()
            )?.apply(event)
        }
    }
}
