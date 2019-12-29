package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.data.KeyRepository
import com.dekitateserver.events.data.entity.Key
import com.dekitateserver.events.domain.vo.KeyId
import com.dekitateserver.events.util.EditArgumentsHelper
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.entity.Player

class CreateKeyUseCase(
        private val keyRepository: KeyRepository
) {
    suspend operator fun invoke(player: Player, keyId: KeyId) {
        val itemStack = try {
            EditArgumentsHelper.getItemInMainHand(player)
        } catch (e: IllegalArgumentException) {
            player.sendWarnMessage(e.message ?: "未知のエラー")
            return
        }

        if (keyRepository.has(keyId)) {
            player.sendWarnMessage("Key(${keyId.value})は既に登録済みです")
            return
        }

        val key = Key(keyId, itemStack.clone())

        if (keyRepository.add(key)) {
            player.sendSuccessMessage("Key(${keyId.value})を作成しました")
        } else {
            player.sendWarnMessage("Key(${keyId.value})の作成に失敗しました")
        }
    }
}
