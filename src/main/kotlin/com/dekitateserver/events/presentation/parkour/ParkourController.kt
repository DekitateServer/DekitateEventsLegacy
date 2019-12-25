package com.dekitateserver.events.presentation.parkour

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.ParkourEditType
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.domain.usecase.eventticket.GiveEventTicketUseCase
import com.dekitateserver.events.domain.usecase.parkour.*
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.util.selectPlayersOrError
import com.dekitateserver.events.util.sendWarnMessage
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender

class ParkourController(plugin: DekitateEventsPlugin) {

    private val server = plugin.server
    private val pluginScope = plugin.pluginScope

    private val joinParkourUseCase = JoinParkourUseCase(
            plugin.parkourRepository,
            plugin.parkourActionHistoryRepository
    )
    private val startParkourUseCase = StartParkourUseCase(
            plugin.parkourRepository,
            plugin.parkourActionHistoryRepository
    )
    private val endParkourUseCase = EndParkourUseCase(
            plugin.parkourRepository,
            plugin.parkourActionHistoryRepository
    )
    private val exitParkourUseCase = ExitParkourUseCase(
            plugin.parkourRepository,
            plugin.parkourActionHistoryRepository
    )
    private val createParkourUseCase = CreateParkourUseCase(plugin.parkourRepository)
    private val deleteParkourUseCase = DeleteParkourUseCase(plugin.parkourRepository)
    private val editParkourUseCase = EditParkourUseCase(plugin.parkourRepository)
    private val sendParkourEditTypeListUseCase = SendParkourEditTypeListUseCase()
    private val sendParkourListUseCase = SendParkourListUseCase(plugin.parkourRepository)

    private val setSpawnUseCase = SetSpawnUseCase()

    private val giveEventTicketUseCase = GiveEventTicketUseCase(plugin.eventTicketHistoryRepository)

    fun join(sender: CommandSender, argSelector: String, argParkourId: String) {
        val parkourId = ParkourId(argParkourId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                val joinParkourUseCaseResult = joinParkourUseCase(player, parkourId) ?: return@forEach

                setSpawnUseCase(
                        player = player,
                        location = joinParkourUseCaseResult.spawnLocation ?: return@forEach
                )
            }
        }
    }

    fun start(sender: CommandSender, argSelector: String, argParkourId: String) {
        val parkourId = ParkourId(argParkourId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                startParkourUseCase(player, parkourId)
            }
        }
    }

    fun end(sender: CommandSender, argSelector: String, argParkourId: String) {
        val parkourId = ParkourId(argParkourId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                val endParkourUseCaseResult = endParkourUseCase(player, parkourId) ?: return@forEach

                setSpawnUseCase(
                        player = player,
                        location = endParkourUseCaseResult.spawnLocation ?: return@forEach
                )

                val eventTicketAmount = endParkourUseCaseResult.eventTicketAmount
                if (eventTicketAmount > 0) {
                    giveEventTicketUseCase(player, eventTicketAmount)
                }
            }
        }
    }

    fun exit(sender: CommandSender, argSelector: String, argParkourId: String) {
        val parkourId = ParkourId(argParkourId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                val exitParkourUseCaseResult = exitParkourUseCase(player, parkourId) ?: return@forEach

                setSpawnUseCase(
                        player = player,
                        location = exitParkourUseCaseResult.spawnLocation ?: return@forEach
                )
            }
        }
    }

    fun create(sender: CommandSender, argParkourId: String, argName: String) {
        pluginScope.launch {
            createParkourUseCase(
                    sender = sender,
                    parkourId = ParkourId(argParkourId),
                    name = argName
            )
        }
    }

    fun delete(sender: CommandSender, argParkourId: String) {
        pluginScope.launch {
            deleteParkourUseCase(
                    sender = sender,
                    parkourId = ParkourId(argParkourId)
            )
        }
    }

    fun edit(sender: CommandSender, argParkourId: String, argType: String, argArgs: List<String>) {
        val parkourEditType = ParkourEditType.find(argType) ?: let {
            sender.sendWarnMessage("存在しないParkourEditTypeです. [type: $argType]")
            return
        }

        pluginScope.launch {
            editParkourUseCase(
                    sender = sender,
                    parkourId = ParkourId(argParkourId),
                    type = parkourEditType,
                    args = argArgs
            )
        }
    }

    fun sendEditTypeList(sender: CommandSender) {
        sendParkourEditTypeListUseCase(sender)
    }

    fun sendList(sender: CommandSender) {
        sendParkourListUseCase(sender)
    }
}
