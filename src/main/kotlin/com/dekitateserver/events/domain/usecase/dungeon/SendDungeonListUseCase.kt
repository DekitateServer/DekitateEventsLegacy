package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.repository.DungeonRepository
import org.bukkit.command.CommandSender

class SendDungeonListUseCase(
        private val dungeonRepository: DungeonRepository
) {
    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("§7--------- §cDungeon一覧 §7---------")

        dungeonRepository.getAll().forEach { dungeon ->
            messageList.add(" ${dungeon.id.value}: ${dungeon.name}")
        }

        sender.sendMessage(messageList.toTypedArray())
    }
}
