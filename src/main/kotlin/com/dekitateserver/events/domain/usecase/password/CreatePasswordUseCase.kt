package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.data.PasswordRepository
import com.dekitateserver.events.data.entity.Password
import com.dekitateserver.events.data.vo.PasswordId
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class CreatePasswordUseCase(
        private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke(sender: CommandSender, passwordId: PasswordId) {
        if (passwordRepository.has(passwordId)) {
            sender.sendWarnMessage("Password(${passwordId.value})は既に登録済みです.")
            return
        }

        val password = Password(passwordId, "")

        if (passwordRepository.add(password)) {
            sender.sendSuccessMessage("Password(${passwordId.value})を作成しました.")
        } else {
            sender.sendWarnMessage("Password(${passwordId.value})の作成に失敗しました.")
        }
    }
}
