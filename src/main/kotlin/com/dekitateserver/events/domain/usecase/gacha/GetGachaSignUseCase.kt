package com.dekitateserver.events.domain.usecase.gacha

import com.dekitateserver.events.domain.repository.SignMetaRepository
import com.dekitateserver.events.domain.vo.GachaCost
import com.dekitateserver.events.domain.vo.GachaId
import org.bukkit.Location

class GetGachaSignUseCase(
        private val signMetaRepository: SignMetaRepository
) : AbstractGachaSignUseCase() {

    operator fun invoke(location: Location): GetGachaSignUseCaseResult? {
        val signMeta = signMetaRepository.getOrError(location) ?: return null

        val gachaId = signMeta.getStringOrError(KEY_SIGN_META_GACHA_ID) ?: return null
        val gachaCost = signMeta.getOrError<GachaCost>(KEY_SIGN_META_GACHA_COST) ?: return null

        return GetGachaSignUseCaseResult(GachaId(gachaId), gachaCost)
    }
}

data class GetGachaSignUseCaseResult(
        val gachaId: GachaId,
        val gachaCost: GachaCost
)
