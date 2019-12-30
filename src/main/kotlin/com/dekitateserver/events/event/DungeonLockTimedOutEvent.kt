package com.dekitateserver.events.event

import com.dekitateserver.events.domain.vo.DungeonId
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class DungeonLockTimedOutEvent(
        val dungeonId: DungeonId
) : Event(true) {

    companion object {
        private val HANDLER_LIST = HandlerList()

        @Suppress("UNUSED")
        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLER_LIST
    }

    override fun getHandlers(): HandlerList = HANDLER_LIST
}
