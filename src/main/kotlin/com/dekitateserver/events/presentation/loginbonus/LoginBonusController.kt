package com.dekitateserver.events.presentation.loginbonus

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.loginbonus.GiveLoginBonusUseCase
import com.dekitateserver.events.domain.usecase.loginbonus.ReloadLoginBonusUseCase
import com.dekitateserver.events.domain.usecase.loginbonus.SendLoginBonusInfoUseCase
import com.dekitateserver.events.domain.usecase.loginbonus.SendLoginBonusListUseCase
import com.dekitateserver.events.domain.vo.LoginBonusId
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LoginBonusController(plugin: DekitateEventsPlugin) {

    private val pluginScope = plugin.pluginScope

    private val giveLoginBonusUseCase = GiveLoginBonusUseCase(
            plugin.server,
            plugin.loginBonusRepository,
            plugin.loginBonusHistoryRepository
    )
    private val sendLoginBonusUseCase = SendLoginBonusInfoUseCase(plugin.loginBonusRepository)
    private val sendLoginBonusListUseCase = SendLoginBonusListUseCase(plugin.loginBonusRepository)
    private val reloadLoginBonusUseCase = ReloadLoginBonusUseCase(plugin.loginBonusRepository)

    fun give(player: Player) {
        pluginScope.launch {
            delay(5000)

            if (player.isOnline) {
                giveLoginBonusUseCase(player)
            }
        }
    }

    fun sendInfo(sender: CommandSender, argLoginBonusId: String) {
        sendLoginBonusUseCase(
                sender = sender,
                loginBonusId = LoginBonusId(argLoginBonusId)
        )
    }

    fun sendList(sender: CommandSender) {
        sendLoginBonusListUseCase(sender)
    }

    fun reload() {
        pluginScope.launch {
            reloadLoginBonusUseCase()
        }
    }
}
