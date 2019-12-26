package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.data.KeyRepository
import com.dekitateserver.events.data.vo.KeyId
import com.dekitateserver.events.util.sendKeyIdNotFound
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class DeleteKeyUseCase(
        private val keyRepository: KeyRepository
) {
    suspend operator fun invoke(sender: CommandSender, keyId: KeyId) {
        if (!keyRepository.has(keyId)) {
            sender.sendKeyIdNotFound(keyId)
            return
        }

        if (keyRepository.remove(keyId)) {
            sender.sendSuccessMessage("Key(${keyId.value})を消去しました")
        } else {
            sender.sendWarnMessage("Key(${keyId.value})の消去に失敗しました")
        }
    }
}
