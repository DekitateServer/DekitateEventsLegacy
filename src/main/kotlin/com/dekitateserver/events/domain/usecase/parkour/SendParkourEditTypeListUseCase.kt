package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.domain.vo.ParkourEditType
import org.bukkit.command.CommandSender

class SendParkourEditTypeListUseCase {

    operator fun invoke(sender: CommandSender) {
        sender.sendMessage(ParkourEditType.HELP_MESSAGES)
    }
}
