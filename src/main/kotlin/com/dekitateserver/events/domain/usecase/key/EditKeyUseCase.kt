package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.data.KeyRepository
import com.dekitateserver.events.data.vo.KeyEditType
import com.dekitateserver.events.data.vo.KeyId
import com.dekitateserver.events.util.*
import org.bukkit.command.CommandSender

class EditKeyUseCase(
        private val keyRepository: KeyRepository
) {
    suspend operator fun invoke(sender: CommandSender, keyId: KeyId, type: KeyEditType, args: List<String>) {
        val key = keyRepository.get(keyId) ?: let {
            sender.sendKeyIdNotFound(keyId)
            return
        }

        val newKey = try {
            when (type) {
                KeyEditType.ITEM -> {
                    val player = sender.toPlayerOrError() ?: return
                    key.copy(itemStackInternal = EditArgumentsHelper.getItemInMainHand(player).clone())
                }
                KeyEditType.TAKE -> {
                    val isEnabled = EditArgumentsHelper.getBoolean(args) ?: false
                    key.copy(isEnabledTake = isEnabled)
                }
                KeyEditType.EXPIRE -> {
                    val minutes = EditArgumentsHelper.getLong(args) ?: 0L
                    key.copy(expireMinutes = minutes)
                }
                KeyEditType.BLOCK_MATERIAL -> key.copy(blockMaterial = EditArgumentsHelper.getBlockMaterial(args))
                KeyEditType.BLOCK_LOCATION -> {
                    val player = sender.toPlayerOrError() ?: return
                    key.copy(blockLocation = EditArgumentsHelper.getLocation(player, args))
                }
                KeyEditType.MATCH_MESSAGE -> key.copy(matchMessage = EditArgumentsHelper.getString(args))
                KeyEditType.NOT_MATCH_MESSAGE -> key.copy(notMatchMessage = EditArgumentsHelper.getString(args))
                KeyEditType.EXPIRED_MESSAGE -> key.copy(expiredMessage = EditArgumentsHelper.getString(args))
                KeyEditType.SHORT_AMOUNT_MESSAGE -> key.copy(shortAmountMessage = EditArgumentsHelper.getString(args))
            }
        } catch (e: IllegalArgumentException) {
            sender.sendWarnMessage(e.message ?: "不明なエラーです")
            return
        }

        if (keyRepository.update(newKey)) {
            sender.sendSuccessMessage("Key(${keyId.value})を更新しました")
        } else {
            sender.sendWarnMessage("Key(${keyId.value})の更新に失敗しました")
        }
    }
}
