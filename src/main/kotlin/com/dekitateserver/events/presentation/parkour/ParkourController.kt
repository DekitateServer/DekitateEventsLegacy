package com.dekitateserver.events.presentation.parkour

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.domain.usecase.parkour.CreateParkourUseCase
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender

class ParkourController(plugin: DekitateEventsPlugin) {

    private val pluginScope = plugin.pluginScope

    private val createParkourUseCase = CreateParkourUseCase(plugin.parkourRepository)

    fun create(sender: CommandSender, argParkourId: String) {
        pluginScope.launch {
            createParkourUseCase(
                    sender = sender,
                    parkourId = ParkourId(argParkourId)
            )
        }
    }
}
