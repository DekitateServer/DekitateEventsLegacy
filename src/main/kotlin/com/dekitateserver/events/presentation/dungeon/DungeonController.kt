package com.dekitateserver.events.presentation.dungeon

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.DungeonId
import com.dekitateserver.events.domain.usecase.dungeon.CreateDungeonUseCase
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender

class DungeonController(plugin: DekitateEventsPlugin) {

    private val pluginScope = plugin.pluginScope

    private val createDungeonUseCase = CreateDungeonUseCase(plugin.dungeonRepository)

    fun create(sender: CommandSender, argDungeonId: String, argName: String) {
        pluginScope.launch {
            createDungeonUseCase(
                    sender = sender,
                    dungeonId = DungeonId(argDungeonId),
                    name = argName
            )
        }
    }
}
