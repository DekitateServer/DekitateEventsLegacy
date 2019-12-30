package com.dekitateserver.events.presentation.dungeon

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.dungeon.*
import com.dekitateserver.events.domain.vo.DungeonEditType
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.selectPlayersOrError
import com.dekitateserver.events.util.sendWarnMessage
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender

class DungeonController(plugin: DekitateEventsPlugin) {

    private val server = plugin.server
    private val pluginScope = plugin.pluginScope

    private val joinDungeonUseCase = JoinDungeonUseCase(plugin.dungeonRepository, plugin.dungeonActionHistoryRepository)
    private val completeDungeonUseCase = CompleteDungeonUseCase(
            server,
            pluginScope,
            plugin.dungeonRepository,
            plugin.dungeonActionHistoryRepository,
            plugin.gachaRepository,
            plugin.gachaHistoryRepository,
            plugin.eventTicketHistoryRepository
    )
    private val exitDungeonUseCase = ExitDungeonUseCase(plugin.dungeonRepository, plugin.dungeonActionHistoryRepository)
    private val createDungeonUseCase = CreateDungeonUseCase(plugin.dungeonRepository)
    private val deleteDungeonUseCase = DeleteDungeonUseCase(plugin.dungeonRepository)
    private val editDungeonUseCase = EditDungeonUseCase(plugin.dungeonRepository)

    fun join(sender: CommandSender, argSelector: String, argDungeonId: String) {
        val dungeonId = DungeonId(argDungeonId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                joinDungeonUseCase(player, dungeonId)
            }
        }
    }

    fun complete(sender: CommandSender, argSelector: String, argDungeonId: String) {
        val dungeonId = DungeonId(argDungeonId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                completeDungeonUseCase(player, dungeonId)
            }
        }
    }

    fun exit(sender: CommandSender, argSelector: String, argDungeonId: String) {
        val dungeonId = DungeonId(argDungeonId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                exitDungeonUseCase(player, dungeonId)
            }
        }
    }

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
