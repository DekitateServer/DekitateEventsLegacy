package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.domain.repository.SignMetaRepository
import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourId
import org.bukkit.Location

class GetParkourSignUseCase(
        private val signMetaRepository: SignMetaRepository
) : AbstractParkourSignUseCase() {

    operator fun invoke(location: Location): GetParkourSignUseCaseResult? {
        val signMeta = signMetaRepository.getOrError(location) ?: return null

        val parkourId = signMeta.getStringOrError(KEY_SIGN_META_PARKOUR_ID) ?: return null
        val parkourAction = signMeta.getStringOrError(KEY_SIGN_META_PARKOUR_ACTION) ?: return null

        return GetParkourSignUseCaseResult(
                parkourId = ParkourId(parkourId),
                parkourAction = ParkourAction.valueOf(parkourAction)
        )
    }
}

data class GetParkourSignUseCaseResult(
        val parkourId: ParkourId,
        val parkourAction: ParkourAction
)
