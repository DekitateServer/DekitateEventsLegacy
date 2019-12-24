package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.data.PasswordRepository
import com.dekitateserver.events.data.vo.PasswordId
import com.dekitateserver.events.util.sendPasswordIdNotFound
import org.bukkit.command.CommandSender

class SendPasswordInfoUseCase(
        private val passwordRepository: PasswordRepository
) {
    operator fun invoke(sender: CommandSender, passwordId: PasswordId) {
        val password = passwordRepository.get(passwordId) ?: let {
            sender.sendPasswordIdNotFound(passwordId)
            return
        }

        val messages = arrayOf(
                "§7--------- §9Password §7---------",
                " ID: ${passwordId.value}",
                " Value: ${password.get()}",
                " Buffer: ${password.bufferText}",
                " BlockMaterial: ${password.blockMaterial?.name}",
                " BlockLocation: ${password.blockLocation}",
                " MatchMessage: ${password.matchMessage}",
                " NotMatchMessage: ${password.notMatchMessage}",
                " InputMessage: ${password.inputMessage}",
                " ResetMessage: ${password.resetMessage}"
        )

        sender.sendMessage(messages)
    }
}
