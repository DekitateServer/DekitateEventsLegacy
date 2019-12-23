package com.dekitateserver.events.domain.usecase.gacha

import com.dekitateserver.events.data.GachaRepository
import org.bukkit.command.CommandSender

class SendGachaListUseCase(
        private val gachaRepository: GachaRepository
) {
    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("§7--------- §6Gacha一覧 §7---------")

        gachaRepository.getAll().forEach { gacha ->
            messageList.add(" ${gacha.id.value}: ${gacha.name}")
        }

        sender.sendMessage(messageList.toTypedArray())
    }
}
