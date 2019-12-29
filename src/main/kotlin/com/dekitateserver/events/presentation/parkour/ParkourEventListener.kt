package com.dekitateserver.events.presentation.parkour

import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

class ParkourEventListener(
        private val parkourController: ParkourController
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val blockState = event.clickedBlock?.state

        if (blockState is Sign && event.action == Action.RIGHT_CLICK_BLOCK && blockState.getLine(0) == ParkourSignContracts.SIGN_INDEX) {
            parkourController.clickSign(event.player, blockState.location)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onSignChanged(event: SignChangeEvent) {
        if (event.getLine(0) == ParkourSignContracts.CREATE_SIGN_INDEX && event.player.hasPermission("neko.event")) {
            parkourController.createSign(
                    player = event.player,
                    location = event.block.location,
                    argParkourId = event.getLine(1).orEmpty(),
                    argParkourAction = event.getLine(3).orEmpty()
            )?.apply(event)
        }
    }
}
