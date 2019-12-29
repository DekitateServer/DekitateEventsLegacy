package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.domain.repository.PasswordRepository
import com.dekitateserver.events.domain.vo.PasswordEditType
import com.dekitateserver.events.domain.vo.PasswordId
import com.dekitateserver.events.util.*
import org.bukkit.command.CommandSender

class EditPasswordUseCase(
        private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke(sender: CommandSender, passwordId: PasswordId, type: PasswordEditType, args: List<String>) {
        val password = passwordRepository.get(passwordId) ?: let {
            sender.sendPasswordIdNotFound(passwordId)
            return
        }

        val newPassword = try {
            when (type) {
                PasswordEditType.BLOCK_MATERIAL -> password.copy(blockMaterial = EditArgumentsHelper.getBlockMaterial(args))
                PasswordEditType.BLOCK_LOCATION -> {
                    val player = sender.toPlayerOrError() ?: return
                    password.copy(blockLocation = EditArgumentsHelper.getLocation(player, args))
                }
                PasswordEditType.MATCH_MESSAGE -> password.copy(matchMessage = EditArgumentsHelper.getString(args))
                PasswordEditType.NOT_MATCH_MESSAGE -> password.copy(notMatchMessage = EditArgumentsHelper.getString(args))
                PasswordEditType.INPUT_MESSAGE -> password.copy(inputMessage = EditArgumentsHelper.getString(args))
                PasswordEditType.RESET_MESSAGE -> password.copy(resetMessage = EditArgumentsHelper.getString(args))
            }
        } catch (e: IllegalArgumentException) {
            sender.sendWarnMessage(e.message ?: "不明なエラーです")
            return
        }

        if (passwordRepository.update(newPassword)) {
            sender.sendSuccessMessage("Password(${passwordId.value})を更新しました")
        } else {
            sender.sendWarnMessage("Password(${passwordId.value})の更新に失敗しました")
        }
    }
}
