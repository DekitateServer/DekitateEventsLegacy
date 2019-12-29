package com.dekitateserver.events.domain.usecase.spawn

import com.dekitateserver.events.infrastructure.repository.SignMetaRepository
import org.bukkit.Location

class GetSetSpawnSignUseCase(
        private val signMetaRepository: SignMetaRepository
) : AbstractSpawnSignUseCase() {

    operator fun invoke(location: Location): GetSetSpawnSignUseCaseResult? {
        val signMeta = signMetaRepository.getOrError(location) ?: return null

        return GetSetSpawnSignUseCaseResult(
                location = signMeta.getOrError(KEY_SIGN_META_SETSPAWN_LOCATION) ?: return null
        )
    }
}

data class GetSetSpawnSignUseCaseResult(
        val location: Location
)
