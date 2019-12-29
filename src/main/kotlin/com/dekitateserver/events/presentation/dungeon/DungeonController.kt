package com.dekitateserver.events.presentation.dungeon

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.dungeon.*
import com.dekitateserver.events.domain.usecase.eventticket.GiveEventTicketUseCase
import com.dekitateserver.events.domain.usecase.gacha.PlayGachaUseCase
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.vo.DungeonEditType
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.selectPlayersOrError
import com.dekitateserver.events.util.sendWarnMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender

class DungeonController(plugin: DekitateEventsPlugin) {

    companion object {
        private const val DELAY_COMPLETE_GACHA = 10000L
    }

    private val server = plugin.server
    private val pluginScope = plugin.pluginScope

    private val joinDungeonUseCase = JoinDungeonUseCase(plugin.dungeonRepository, plugin.dungeonActionHistoryRepository)
    private val completeDungeonUseCase = CompleteDungeonUseCase(plugin.dungeonRepository, plugin.dungeonActionHistoryRepository)
    private val createDungeonUseCase = CreateDungeonUseCase(plugin.dungeonRepository)
    private val deleteDungeonUseCase = DeleteDungeonUseCase(plugin.dungeonRepository)
    private val editDungeonUseCase = EditDungeonUseCase(plugin.dungeonRepository)

    private val setSpawnUseCase = SetSpawnUseCase()

    private val giveEventTicketUseCase = GiveEventTicketUseCase(plugin.eventTicketHistoryRepository)

    private val playGachaUseCase = PlayGachaUseCase(plugin.server, plugin.gachaRepository, plugin.gachaHistoryRepository)

    fun join(sender: CommandSender, argSelector: String, argDungeonId: String) {
        val dungeonId = DungeonId(argDungeonId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                val joinDungeonUseCaseResult = joinDungeonUseCase(player, dungeonId) ?: return@forEach

                setSpawnUseCase(
                        player = player,
                        location = joinDungeonUseCaseResult.spawnLocation ?: return@forEach
                )
            }
        }
    }

    fun complete(sender: CommandSender, argSelector: String, argDungeonId: String) {
        val dungeonId = DungeonId(argDungeonId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                val completeDungeonUseCaseResult = completeDungeonUseCase(player, dungeonId) ?: return@forEach

                if (completeDungeonUseCaseResult.spawnLocation != null) {
                    setSpawnUseCase(
                            player = player,
                            location = completeDungeonUseCaseResult.spawnLocation
                    )
                }

                val eventTicketAmount = completeDungeonUseCaseResult.eventTicketAmount
                if (eventTicketAmount > 0) {
                    giveEventTicketUseCase(player, eventTicketAmount)
                }

                if (completeDungeonUseCaseResult.gachaId != null) {
                    launch {
                        delay(DELAY_COMPLETE_GACHA)
                        playGachaUseCase(
                                player = player,
                                gachaId = completeDungeonUseCaseResult.gachaId
                        )
                    }
                }
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
