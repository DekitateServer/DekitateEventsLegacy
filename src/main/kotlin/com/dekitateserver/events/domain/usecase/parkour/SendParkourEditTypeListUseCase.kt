package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.data.vo.ParkourEditType
import org.bukkit.command.CommandSender

class SendParkourEditTypeListUseCase {

    operator fun invoke(sender: CommandSender) {
        val messageList = mutableListOf("--------- ParkourEditType ---------")

        ParkourEditType.values().forEach {
            messageList.add("| §9${it.id} ${it.description}")
        }

        messageList.add("| §7必須: [], 任意: <>")
        messageList.add("---------------------------------------")

        sender.sendMessage(messageList.toTypedArray())
    }
}
