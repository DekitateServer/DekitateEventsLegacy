package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.data.KeyRepository
import com.dekitateserver.events.domain.vo.KeyId
import com.dekitateserver.events.util.addItemOrDrop
import org.bukkit.entity.Player

class GiveKeyUseCase(
        private val keyRepository: KeyRepository
) {
    operator fun invoke(player: Player, keyId: KeyId, amount: Int) {
        val key = keyRepository.getOrError(keyId) ?: return
        val itemStack = key.createItemStack(amount)

        player.addItemOrDrop(itemStack)
    }
}
