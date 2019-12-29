package com.dekitateserver.events.domain.vo

import org.bukkit.event.block.SignChangeEvent

data class SignLines(
        val line0: String,
        val line1: String,
        val line2: String,
        val line3: String
) {
    fun apply(event: SignChangeEvent) = event.apply {
        setLine(0, line0)
        setLine(1, line1)
        setLine(2, line2)
        setLine(3, line3)
    }
}
