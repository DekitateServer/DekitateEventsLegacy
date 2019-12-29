package com.dekitateserver.events.presentation.parkour

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.parkour.*
import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourEditType
import com.dekitateserver.events.domain.vo.ParkourId
import com.dekitateserver.events.domain.vo.SignLines
import com.dekitateserver.events.util.selectPlayersOrError
import com.dekitateserver.events.util.sendWarnMessage
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
            plugin.parkourActionHistoryRepository,
            plugin.eventTicketHistoryRepository
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
    private val sendParkourInfoUseCase = SendParkourInfoUseCase(plugin.parkourRepository)
    private val reloadParkourUseCategory = ReloadParkourUseCase(plugin.parkourRepository)
    private val clickParkourSignUseCase = ClickParkourSignUseCase(
            plugin.parkourRepository,
            plugin.signMetaRepository,
            plugin.parkourActionHistoryRepository,
            plugin.eventTicketHistoryRepository
    )
    private val createParkourSignUseCase = CreateParkourSignUseCase(plugin.parkourRepository, plugin.signMetaRepository)

    fun join(sender: CommandSender, argSelector: String, argParkourId: String) {
        val parkourId = ParkourId(argParkourId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                joinParkourUseCase(player, parkourId)
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
                endParkourUseCase(player, parkourId)
            }
        }
    }

    fun exit(sender: CommandSender, argSelector: String, argParkourId: String) {
        val parkourId = ParkourId(argParkourId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                exitParkourUseCase(player, parkourId)
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
            sender.sendWarnMessage("存在しないParkourEditTypeです [type: $argType]")
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

    fun sendInfo(sender: CommandSender, argParkourId: String) {
        sendParkourInfoUseCase(
                sender = sender,
                parkourId = ParkourId(argParkourId)
        )
    }

    fun reload() {
        pluginScope.launch {
            reloadParkourUseCategory()
        }
    }

    fun clickSign(player: Player, location: Location) {
        pluginScope.launch {
            clickParkourSignUseCase(player, location)
        }
    }

    fun createSign(player: Player, location: Location, argParkourId: String, argParkourAction: String): SignLines? {
        val action = try {
            ParkourAction.valueOf(argParkourAction.toUpperCase())
        } catch (e: IllegalArgumentException) {
            player.sendWarnMessage("そのようなParkourActionは存在しません")
            return null
        }

        val createParkourSignUseCaseResult = createParkourSignUseCase(
                location = location,
                player = player,
                parkourId = ParkourId(argParkourId),
                action = action
        ) ?: return null

        return createParkourSignUseCaseResult.signLines
    }
}
