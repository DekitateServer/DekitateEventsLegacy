package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.domain.vo.ParkourEditType
import org.bukkit.command.CommandSender

class SendParkourEditTypeListUseCase {

    operator fun invoke(sender: CommandSender) {
        val messages = mutableListOf("--------- ParkourEditType ---------").apply {
            ParkourEditType.values().forEach {
                add("| §9${it.id} ${it.description}")
            }

            add("| §7必須: [], 任意: <>")
            add("---------------------------------------")
        }.toTypedArray()

        sender.sendMessage(messages)
    }
}
