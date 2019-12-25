package com.dekitateserver.events.presentation.spawn

import com.dekitateserver.events.DekitateEvents
import org.bukkit.GameMode
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractEvent

class SpawnEventListener(
        private val spawnController: SpawnController
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val blockState = event.clickedBlock?.state

        if (blockState is Sign && event.action == Action.RIGHT_CLICK_BLOCK && blockState.getLine(0) == SpawnSignContracts.SIGN_INDEX) {
            spawnController.clickSign(event.player, blockState.location)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onSignChanged(event: SignChangeEvent) {
        if (event.getLine(0) == SpawnSignContracts.CREATE_SIGN_INDEX && event.player.hasPermission("neko.event")) {
            spawnController.createSign(event)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        if (event.player.gameMode == GameMode.ADVENTURE && event.from.name == DekitateEvents.NAME_EVENT_WORLD) {
            spawnController.removeBedSpawnIfInEvents(event.player)
        }
    }
}
