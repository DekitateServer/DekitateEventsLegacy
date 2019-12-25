package com.dekitateserver.events.presentation.gacha

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.GachaCost
import com.dekitateserver.events.data.vo.GachaId
import com.dekitateserver.events.data.vo.KeyId
import com.dekitateserver.events.domain.usecase.eventticket.TakeEventTicketUseCase
import com.dekitateserver.events.domain.usecase.gacha.*
import com.dekitateserver.events.domain.usecase.key.UseKeyUseCase
import com.dekitateserver.events.domain.usecase.voteticket.TakeVoteTicketUseCase
import com.dekitateserver.events.util.Log
import com.dekitateserver.events.util.selectPlayersOrError
import com.dekitateserver.events.util.toIntOrError
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.block.SignChangeEvent

class GachaController(plugin: DekitateEventsPlugin) {

    private val server = plugin.server
    private val pluginScope = plugin.pluginScope

    private val playGachaUseCase = PlayGachaUseCase(plugin.server, plugin.gachaRepository, plugin.gachaHistoryRepository)
    private val demoGachaUseCase = DemoGachaUseCase(plugin.gachaRepository)
    private val sendGachaListUseCase = SendGachaListUseCase(plugin.gachaRepository)
    private val sendGachaInfoUseCase = SendGachaInfoUseCase(plugin.gachaRepository)
    private val reloadGachaUseCase = ReloadGachaUseCase(plugin.gachaRepository)
    private val getGachaSignUseCase = GetGachaSignUseCase(plugin.signMetaRepository)
    private val createGachaSignUseCase = CreateGachaSignUseCase(plugin.gachaRepository, plugin.keyRepository, plugin.signMetaRepository)

    private val takeEventTicketUseCase = TakeEventTicketUseCase(plugin.eventTicketHistoryRepository, plugin.voteTicketHistoryRepository)

    private val takeVoteTicketUseCase = TakeVoteTicketUseCase(plugin.voteTicketHistoryRepository)

    private val useKeyUseCase = UseKeyUseCase(plugin.keyRepository)

    fun play(sender: CommandSender, argSelector: String, argGachaId: String) {
        val gachaId = GachaId(argGachaId)

        pluginScope.launch {
            server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
                playGachaUseCase(player, gachaId)
            }
        }
    }

    fun demo(sender: CommandSender, argGachaId: String, argTimes: String) {
        demoGachaUseCase(
                sender = sender,
                gachaId = GachaId(argGachaId),
                times = argTimes.toIntOrError() ?: return
        )
    }

    fun sendList(sender: CommandSender) {
        sendGachaListUseCase(sender)
    }

    fun sendInfo(sender: CommandSender, argGachaId: String) {
        sendGachaInfoUseCase(sender, GachaId(argGachaId))
    }

    fun reload() {
        pluginScope.launch {
            reloadGachaUseCase()
        }
    }

    fun clickSign(player: Player, location: Location) {
        val getGachaSignUseCaseResult = getGachaSignUseCase(location) ?: return

        pluginScope.launch {
            when (val cost = getGachaSignUseCaseResult.gachaCost) {
                is GachaCost.EventTicket -> {
                    if (!takeEventTicketUseCase(player, cost.amount)) {
                        return@launch
                    }
                }
                is GachaCost.VoteTicket -> {
                    if (!takeVoteTicketUseCase(player, cost.amount)) {
                        return@launch
                    }
                }
                is GachaCost.Key -> {
                    if (!useKeyUseCase(player, cost.keyId)) {
                        return@launch
                    }
                }
                GachaCost.Free -> Unit
            }

            playGachaUseCase(player, getGachaSignUseCaseResult.gachaId)
        }
    }

    fun createSign(event: SignChangeEvent) {
        val gachaCost = when (event.getLine(2)) {
            "free" -> GachaCost.Free
            "event" -> GachaCost.EventTicket(
                    amount = event.getLine(3).orEmpty().toIntOrError() ?: return
            )
            "vote" -> GachaCost.VoteTicket(
                    amount = event.getLine(3).orEmpty().toIntOrError() ?: return
            )
            "key" -> GachaCost.Key(
                    keyId = KeyId(event.getLine(3).orEmpty())
            )
            else -> {
                Log.error("GachaCost(${event.getLine(2)})は存在しません.")
                Log.error("free, event, vote, keyを指定してください.")

                return
            }
        }

        val signLines = createGachaSignUseCase(
                location = event.block.location,
                player = event.player,
                gachaId = GachaId(event.getLine(1).orEmpty()),
                gachaCost = gachaCost
        ) ?: return

        signLines.apply(event)
    }
}
