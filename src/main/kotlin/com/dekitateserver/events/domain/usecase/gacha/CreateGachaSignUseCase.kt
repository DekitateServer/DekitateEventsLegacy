package com.dekitateserver.events.domain.usecase.gacha

import com.dekitateserver.events.domain.repository.GachaRepository
import com.dekitateserver.events.domain.repository.KeyRepository
import com.dekitateserver.events.domain.repository.SignMetaRepository
import com.dekitateserver.events.domain.vo.GachaCost
import com.dekitateserver.events.domain.vo.GachaId
import com.dekitateserver.events.domain.vo.SignLines
import com.dekitateserver.events.presentation.gacha.GachaSignContracts
import com.dekitateserver.events.util.sendGachaIdNotFound
import com.dekitateserver.events.util.sendSuccessMessage
import kotlinx.coroutines.runBlocking
import org.bukkit.Location
import org.bukkit.entity.Player

class CreateGachaSignUseCase(
        private val gachaRepository: GachaRepository,
        private val keyRepository: KeyRepository,
        private val signMetaRepository: SignMetaRepository
) : AbstractGachaSignUseCase() {

    operator fun invoke(location: Location, player: Player, gachaId: GachaId, gachaCost: GachaCost): CreateGachaSignUseCaseResult? {
        val gacha = gachaRepository.get(gachaId) ?: let {
            player.sendGachaIdNotFound(gachaId)
            return null
        }

        val signMeta = signMetaRepository.getOrNew(location).apply {
            put(KEY_SIGN_META_GACHA_ID, gachaId.value)
            put(KEY_SIGN_META_GACHA_COST, gachaCost)
        }

        val costDetailLine: String = when (gachaCost) {
            is GachaCost.EventTicket -> "§9§n1プレイ§r §a§n${gachaCost.amount}枚"
            is GachaCost.VoteTicket -> "§9§n1プレイ§r §a§n${gachaCost.amount}枚"
            is GachaCost.Key -> {
                val key = keyRepository.getOrError(gachaCost.keyId) ?: return null
                key.name
            }
            GachaCost.Free -> "§c無料"
        }

        val isAddSuccessful = runBlocking {
            signMetaRepository.add(signMeta)
        }

        return if (isAddSuccessful) {
            player.sendSuccessMessage("看板を作成しました")

            val signLines = SignLines(
                    line0 = GachaSignContracts.SIGN_INDEX,
                    line1 = gacha.name,
                    line2 = "",
                    line3 = costDetailLine
            )

            CreateGachaSignUseCaseResult(signLines)
        } else null
    }
}

data class CreateGachaSignUseCaseResult(
        val signLines: SignLines
)
