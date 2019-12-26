package com.dekitateserver.events.domain.usecase.revive

import com.dekitateserver.events.data.SignMetaRepository
import org.bukkit.Location

class GetReviveSignUseCase(
        private val signMetaRepository: SignMetaRepository
) : AbstractReviveSignUseCase() {

    operator fun invoke(location: Location): GetReviveSignUseCaseResult? {
        val signMeta = signMetaRepository.getOrError(location) ?: return null

        return GetReviveSignUseCaseResult(
                location = signMeta.getOrError(KEY_SIGN_META_REVIVE_LOCATION) ?: return null,
                experience = signMeta.getIntOrError(KEY_SIGN_META_REVIVE_EXPERIENCE) ?: return null
        )
    }
}

data class GetReviveSignUseCaseResult(
        val location: Location,
        val experience: Int
)
