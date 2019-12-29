package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.domain.vo.KeyEditType
import org.bukkit.command.CommandSender

class SendKeyEditTypeListUseCase {

    operator fun invoke(sender: CommandSender) {
        sender.sendMessage(KeyEditType.HELP_MESSAGES)
    }
}
