package com.dekitateserver.events.domain.usecase.loginbonus

import com.dekitateserver.core.bukkit.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.LoginBonusHistoryRepository
import com.dekitateserver.events.domain.repository.LoginBonusRepository
import org.bukkit.Server
import org.bukkit.entity.Player

class GiveLoginBonusUseCase(
        private val server: Server,
        private val loginBonusRepository: LoginBonusRepository,
        private val loginBonusHistoryRepository: LoginBonusHistoryRepository
) {
    private val consoleCommandSender = server.consoleSender

    suspend operator fun invoke(player: Player) {
        loginBonusRepository.getListByNow().forEach { loginBonus ->
            if (loginBonusHistoryRepository.has(player, loginBonus.id)) {
                return@forEach
            }

            server.dispatchCommand(consoleCommandSender, loginBonus.command.replace("{player}", player.name))
            player.sendMessageIfNotNull(loginBonus.message)

            loginBonusHistoryRepository.add(player, loginBonus.id)
        }
    }
}
