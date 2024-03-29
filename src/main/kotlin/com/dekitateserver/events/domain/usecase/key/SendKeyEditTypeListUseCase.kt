package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.domain.vo.KeyEditType
import org.bukkit.command.CommandSender

class SendKeyEditTypeListUseCase {

    operator fun invoke(sender: CommandSender) {
        val messages = mutableListOf("--------- KeyEditType ---------").apply {
            KeyEditType.values().forEach {
                add("| §9${it.id} ${it.description}")
            }

            add("| §7必須: [], 任意: <>")
            add("---------------------------------------")
        }.toTypedArray()

        sender.sendMessage(messages)
    }
}
