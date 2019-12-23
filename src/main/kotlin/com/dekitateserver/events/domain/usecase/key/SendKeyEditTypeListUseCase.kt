package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.data.vo.KeyEditType
import org.bukkit.command.CommandSender

class SendKeyEditTypeListUseCase {

    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("--------- KeyEditType ---------")

        KeyEditType.values().forEach {
            messageList.add("| §9${it.id} ${it.description}")
        }

        messageList.add("| §7必須: [], 任意: <>")
        messageList.add("---------------------------------------")

        sender.sendMessage(messageList.toTypedArray())
    }
}
