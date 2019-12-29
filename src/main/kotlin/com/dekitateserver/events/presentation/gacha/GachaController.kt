package com.dekitateserver.events.presentation.gacha

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.gacha.*
import com.dekitateserver.events.domain.vo.GachaCost
import com.dekitateserver.events.domain.vo.GachaId
import com.dekitateserver.events.domain.vo.KeyId
import com.dekitateserver.events.domain.vo.SignLines
import com.dekitateserver.events.util.Log
import com.dekitateserver.events.util.selectPlayersOrError
import com.dekitateserver.events.util.toIntOrError
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GachaController(plugin: DekitateEventsPlugin) {

    private val server = plugin.server
    private val pluginScope = plugin.pluginScope

    private val playGachaUseCase = PlayGachaUseCase(plugin.server, plugin.gachaRepository, plugin.gachaHistoryRepository)
    private val demoGachaUseCase = DemoGachaUseCase(plugin.gachaRepository)
    private val sendGachaListUseCase = SendGachaListUseCase(plugin.gachaRepository)
    private val sendGachaInfoUseCase = SendGachaInfoUseCase(plugin.gachaRepository)
    private val reloadGachaUseCase = ReloadGachaUseCase(plugin.gachaRepository)
    private val clickGachaSignUseCase = ClickGachaSignUseCase(
            plugin.server,
            plugin.gachaRepository,
            plugin.keyRepository,
            plugin.gachaHistoryRepository,
            plugin.eventTicketHistoryRepository,
            plugin.voteTicketHistoryRepository,
            plugin.signMetaRepository
    )
    private val createGachaSignUseCase = CreateGachaSignUseCase(plugin.gachaRepository, plugin.keyRepository, plugin.signMetaRepository)

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
        pluginScope.launch {
            clickGachaSignUseCase(player, location)
        }
    }

    fun createSign(player: Player, location: Location, argGachaId: String, argGachaCost: String, argGachaCostParameter: String): SignLines? {
        val gachaCost = when (argGachaCost) {
            "free" -> GachaCost.Free
            "event" -> GachaCost.EventTicket(
                    amount = argGachaCostParameter.toIntOrError() ?: return null
            )
            "vote" -> GachaCost.VoteTicket(
                    amount = argGachaCostParameter.toIntOrError() ?: return null
            )
            "key" -> GachaCost.Key(
                    keyId = KeyId(argGachaCostParameter)
            )
            else -> {
                Log.error("GachaCost($argGachaCost)は存在しません")
                Log.error("(free|event|vote|key)を指定してください")

                return null
            }
        }

        val createGachaSignUseCaseResult = createGachaSignUseCase(
                location = location,
                player = player,
                gachaId = GachaId(argGachaId),
                gachaCost = gachaCost
        ) ?: return null

        return createGachaSignUseCaseResult.signLines
    }
}
