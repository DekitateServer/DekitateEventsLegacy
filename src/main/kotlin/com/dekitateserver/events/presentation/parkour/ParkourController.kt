package com.dekitateserver.events.presentation.parkour

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.ParkourEditType
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.domain.usecase.parkour.CreateParkourUseCase
import com.dekitateserver.events.domain.usecase.parkour.DeleteParkourUseCase
import com.dekitateserver.events.domain.usecase.parkour.EditParkourUseCase
import com.dekitateserver.events.domain.usecase.parkour.JoinParkourUseCase
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
    private val createParkourUseCase = CreateParkourUseCase(plugin.parkourRepository)
    private val deleteParkourUseCase = DeleteParkourUseCase(plugin.parkourRepository)
    private val editParkourUseCase = EditParkourUseCase(plugin.parkourRepository)

    private val setSpawnUseCase = SetSpawnUseCase()

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
}
