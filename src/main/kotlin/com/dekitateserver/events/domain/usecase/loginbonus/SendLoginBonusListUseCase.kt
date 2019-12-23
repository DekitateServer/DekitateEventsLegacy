package com.dekitateserver.events.domain.usecase.loginbonus

import com.dekitateserver.events.data.LoginBonusRepository
import org.bukkit.command.CommandSender

class SendLoginBonusListUseCase(
        private val loginBonusRepository: LoginBonusRepository
) {
    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("§7--------- §6LoginBonus一覧 §7---------")

        loginBonusRepository.getAll().forEach { loginBonus ->
            messageList.add(" ${loginBonus.id.value}: ${loginBonus.start} ~ ${loginBonus.end}")
        }

        sender.sendMessage(messageList.toTypedArray())
    }
}
