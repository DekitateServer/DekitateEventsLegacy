package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.data.PasswordRepository
import org.bukkit.command.CommandSender

class SendPasswordListUseCase(
        private val passwordRepository: PasswordRepository
) {
    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("§7--------- §9Password一覧 §7---------")

        passwordRepository.getAll().forEach { password ->
            messageList.add(" ${password.id.value}: ${password.get()}")
        }

        sender.sendMessage(messageList.toTypedArray())
    }
}
