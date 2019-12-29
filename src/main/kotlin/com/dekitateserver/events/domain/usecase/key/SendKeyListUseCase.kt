package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.domain.repository.KeyRepository
import org.bukkit.command.CommandSender

class SendKeyListUseCase(
        private val keyRepository: KeyRepository
) {
    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("§7--------- §6Key一覧 §7---------")

        keyRepository.getAll().forEach { key ->
            messageList.add(" ${key.id.value}: ${key.name}")
        }

        sender.sendMessage(messageList.toTypedArray())
    }
}
