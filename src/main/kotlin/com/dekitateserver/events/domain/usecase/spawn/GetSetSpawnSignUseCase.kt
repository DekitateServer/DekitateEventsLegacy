package com.dekitateserver.events.domain.usecase.spawn

import com.dekitateserver.events.data.SignMetaRepository
import org.bukkit.Location

class GetSetSpawnSignUseCase(
        private val signMetaRepository: SignMetaRepository
) : AbstractSpawnSignUseCase() {

    operator fun invoke(location: Location): Location? {
        val signMeta = signMetaRepository.getOrError(location) ?: return null
        return signMeta.getStringOrError<Location>(KEY_SIGN_META_SETSPAWN_LOCATION)
    }
}
