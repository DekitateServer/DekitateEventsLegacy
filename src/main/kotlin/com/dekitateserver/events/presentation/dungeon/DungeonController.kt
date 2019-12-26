package com.dekitateserver.events.presentation.dungeon

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.DungeonEditType
import com.dekitateserver.events.data.vo.DungeonId
import com.dekitateserver.events.domain.usecase.dungeon.CreateDungeonUseCase
import com.dekitateserver.events.domain.usecase.dungeon.DeleteDungeonUseCase
import com.dekitateserver.events.domain.usecase.dungeon.EditDungeonUseCase
import com.dekitateserver.events.util.sendWarnMessage
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender

class DungeonController(plugin: DekitateEventsPlugin) {

    private val pluginScope = plugin.pluginScope

    private val createDungeonUseCase = CreateDungeonUseCase(plugin.dungeonRepository)
    private val deleteDungeonUseCase = DeleteDungeonUseCase(plugin.dungeonRepository)
    private val editDungeonUseCase = EditDungeonUseCase(plugin.dungeonRepository)

    fun create(sender: CommandSender, argDungeonId: String, argName: String) {
        pluginScope.launch {
            createDungeonUseCase(
                    sender = sender,
                    dungeonId = DungeonId(argDungeonId),
                    name = argName
            )
        }
    }

    fun delete(sender: CommandSender, argDungeonId: String) {
        pluginScope.launch {
            deleteDungeonUseCase(
                    sender = sender,
                    dungeonId = DungeonId(argDungeonId)
            )
        }
    }

    fun edit(sender: CommandSender, argDungeonId: String, argType: String, argArgs: List<String>) {
        val dungeonEditType = DungeonEditType.find(argType) ?: let {
            sender.sendWarnMessage("存在しないDungeonEditTypeです [type: $argType]")
            return
        }

        pluginScope.launch {
            editDungeonUseCase(
                    sender = sender,
                    dungeonId = DungeonId(argDungeonId),
                    type = dungeonEditType,
                    args = argArgs
            )
        }
    }
}
