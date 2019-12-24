package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.data.vo.PasswordEditType
import org.bukkit.command.CommandSender

class SendPasswordEditTypeListUseCase {

    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("--------- PasswordEditType ---------")

        PasswordEditType.values().forEach {
            messageList.add("| §9${it.id} ${it.description}")
        }

        messageList.add("| §7必須: [], 任意: <>")
        messageList.add("---------------------------------------")

        sender.sendMessage(messageList.toTypedArray())
    }
}
