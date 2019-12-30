package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.vo.DungeonEditType
import org.bukkit.command.CommandSender

class SendDungeonEditTypeListUseCase {

    operator fun invoke(sender: CommandSender) {
        val messages = mutableListOf("--------- DungeonEditType ---------").apply {
            DungeonEditType.values().forEach {
                add("| §9${it.id} ${it.description}")
            }

            add("| §7必須: [], 任意: <>")
            add("---------------------------------------")
        }.toTypedArray()

        sender.sendMessage(messages)
    }
}
