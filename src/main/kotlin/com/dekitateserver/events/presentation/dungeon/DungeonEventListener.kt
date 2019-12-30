package com.dekitateserver.events.presentation.dungeon

import com.dekitateserver.events.event.DungeonLockTimedOutEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class DungeonEventListener(
        private val dungeonController: DungeonController
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDungeonLockTimedOut(event: DungeonLockTimedOutEvent) {
        dungeonController.unlock(event.dungeonId)
    }
}
