package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.data.PasswordRepository
import com.dekitateserver.events.data.vo.PasswordId
import com.dekitateserver.events.util.sendPasswordIdNotFound
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class DeletePasswordUseCase(
        private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke(sender: CommandSender, passwordId: PasswordId) {
        if (!passwordRepository.has(passwordId)) {
            sender.sendPasswordIdNotFound(passwordId)
            return
        }

        if (passwordRepository.remove(passwordId)) {
            sender.sendSuccessMessage("Password(${passwordId.value})を消去しました")
        } else {
            sender.sendWarnMessage("Password(${passwordId.value})の消去に失敗しました")
        }
    }
}
