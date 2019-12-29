package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.domain.repository.ParkourRepository
import org.bukkit.command.CommandSender

class SendParkourListUseCase(
        private val parkourRepository: ParkourRepository
) {
    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("§7--------- §aParkour一覧 §7---------")

        parkourRepository.getAll().forEach { parkour ->
            messageList.add(" ${parkour.id.value}: ${parkour.name}")
        }

        sender.sendMessage(messageList.toTypedArray())
    }
}
