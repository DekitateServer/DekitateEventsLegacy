package com.dekitateserver.events.domain.usecase.loginbonus

import com.dekitateserver.events.domain.repository.LoginBonusRepository
import com.dekitateserver.events.domain.vo.LoginBonusId
import com.dekitateserver.events.util.sendLoginBonusIdNotFound
import org.bukkit.command.CommandSender

class SendLoginBonusInfoUseCase(
        private val loginBonusRepository: LoginBonusRepository
) {
    operator fun invoke(sender: CommandSender, loginBonusId: LoginBonusId) {
        val loginBonus = loginBonusRepository.get(loginBonusId) ?: let {
            sender.sendLoginBonusIdNotFound(loginBonusId)
            return
        }

        val messages = arrayOf(
                "§7--------- §6LoginBonus §7---------",
                " ID: ${loginBonusId.value}",
                " Start: ${loginBonus.start}",
                " End: ${loginBonus.end}",
                " Command: ${loginBonus.command}",
                " Message: ${loginBonus.message}"
        )

        sender.sendMessage(messages)
    }
}
