package com.dekitateserver.events.presentation.loginbonus

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class LoginBonusEventListener(
        private val loginBonusController: LoginBonusController
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        loginBonusController.give(event.player)
    }
}
