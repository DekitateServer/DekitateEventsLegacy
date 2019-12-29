package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.data.KeyRepository
import com.dekitateserver.events.domain.vo.KeyId
import com.dekitateserver.events.util.sendKeyIdNotFound
import org.bukkit.command.CommandSender

class SendKeyInfoUseCase(
        private val keyRepository: KeyRepository
) {
    operator fun invoke(sender: CommandSender, keyId: KeyId) {
        val key = keyRepository.get(keyId) ?: let {
            sender.sendKeyIdNotFound(keyId)
            return
        }

        val messages = arrayOf(
                "§7--------- §6Key §7---------",
                " ID: ${keyId.value}",
                " Name: ${key.name}",
                " EnabledTake: ${key.isEnabledTake}",
                " ExpireMinutes: ${key.expireMinutes}",
                " BlockMaterial: ${key.blockMaterial?.name}",
                " BlockLocation: ${key.blockLocation}",
                " MatchMessage: ${key.matchMessage}",
                " NotMatchMessage: ${key.notMatchMessage}",
                " ExpiredMessage: ${key.expiredMessage}",
                " ShortAmountMessage: ${key.shortAmountMessage}"
        )

        sender.sendMessage(messages)
    }
}
