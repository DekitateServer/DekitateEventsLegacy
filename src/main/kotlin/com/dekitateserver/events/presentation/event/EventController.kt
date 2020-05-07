package com.dekitateserver.events.presentation.event

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.eventticket.GiveEventTicketUseCase
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.usecase.voteticket.GiveVoteTicketUseCase
import com.dekitateserver.events.util.*
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender

class EventController(plugin: DekitateEventsPlugin) {

    private val server = plugin.server
    private val pluginScope = plugin.pluginScope

    private val giveEventTicketUseCase = GiveEventTicketUseCase(plugin.eventTicketHistoryRepository)

    private val giveVoteTicketUseCase = GiveVoteTicketUseCase(plugin.voteTicketHistoryRepository)

    private val setSpawnUseCase = SetSpawnUseCase()

    fun giveTicket(sender: CommandSender, argSelector: String, argTicketType: String, argAmount: String) {
        val playerList = server.selectPlayersOrError(sender, argSelector) ?: return
        val amount = argAmount.toIntOrError() ?: return

        pluginScope.launch {
            when (argTicketType) {
                "event" -> playerList.forEach { giveEventTicketUseCase(it, amount) }
                "vote" -> playerList.forEach { giveVoteTicketUseCase(it, amount) }
                else -> {
                    Log.warn("正しいチケットタイプ(event|vote)を指定してください")
                    return@launch
                }
            }
        }
    }

    fun teleport(sender: CommandSender, argSelector: String, argX: String, argY: String, argZ: String, argYaw: String?, argPitch: String?) {

    }

    fun setSpawn(sender: CommandSender, argSelector: String, argX: String, argY: String, argZ: String) {
        if (sender !is BlockCommandSender) {
            sender.sendCommandBlockOnly()
            return
        }

        val location = Location(
            sender.block.world,
            argX.toDoubleOrError() ?: return,
            argY.toDoubleOrError() ?: return,
            argZ.toDoubleOrError() ?: return
        )

        server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
            setSpawnUseCase(player, location)
        }
    }

    fun sendUnsupported(sender: CommandSender) {

    }

    private fun createLocation(pivot: Location, x: String, y: String, z: String, yaw: String?, pitch: String?) {
        Location(
            pivot.world,
            parseCoordinate(pivot.x, x) ?: return,
            parseCoordinate(pivot.y, y) ?: return,
            parseCoordinate(pivot.z, z) ?: return
        )
    }

    private fun parseCoordinate(p: Double, arg: String): Double? {
        if (arg.startsWith('~')) {
            val sub = arg.substring(1)

            return if (sub.isNotEmpty()) {
                sub.toDoubleOrError()?.plus(p)
            } else p
        }

        return arg.toDoubleOrError()
    }
}
