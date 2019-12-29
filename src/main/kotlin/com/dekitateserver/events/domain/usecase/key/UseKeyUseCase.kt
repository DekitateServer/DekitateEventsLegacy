package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.KeyRepository
import com.dekitateserver.events.domain.vo.KeyCompareResult
import com.dekitateserver.events.domain.vo.KeyId
import org.bukkit.entity.Player

class UseKeyUseCase(
        private val keyRepository: KeyRepository
) {
    operator fun invoke(player: Player, keyId: KeyId): Boolean {
        val key = keyRepository.getOrError(keyId) ?: return false

        val itemInMainHand = player.inventory.itemInMainHand

        when (key.compareTo(itemInMainHand)) {
            KeyCompareResult.MATCH -> {
                if (key.isEnabledTake) {
                    val amount = itemInMainHand.amount - key.amount
                    player.inventory.itemInMainHand.amount = amount
                }

                key.blockMaterialLocation?.let {
                    it.location.block.type = it.material
                }

                player.sendMessageIfNotNull(key.matchMessage)
                return true
            }
            KeyCompareResult.NOT_MATCH -> player.sendMessageIfNotNull(key.notMatchMessage)
            KeyCompareResult.EXPIRED -> player.sendMessageIfNotNull(key.expiredMessage)
            KeyCompareResult.SHORT_AMOUNT -> player.sendMessageIfNotNull(key.shortAmountMessage)
        }

        return false
    }
}
