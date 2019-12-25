package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.data.SignMetaRepository
import com.dekitateserver.events.data.vo.ParkourAction
import com.dekitateserver.events.data.vo.ParkourId
import org.bukkit.Location

class GetParkourSignUseCase(
        private val signMetaRepository: SignMetaRepository
) : AbstractParkourSignUseCase() {

    operator fun invoke(location: Location): GetParkourSignUseCaseResult? {
        val signMeta = signMetaRepository.getOrError(location) ?: return null

        val parkourId = signMeta.getOrError(KEY_SIGN_META_PARKOUR_ID) ?: return null
        val parkourAction = signMeta.getOrError<ParkourAction>(KEY_SIGN_META_PARKOUR_ACTION) ?: return null

        return GetParkourSignUseCaseResult(
                parkourId = ParkourId(parkourId),
                parkourAction = parkourAction
        )
    }
}

data class GetParkourSignUseCaseResult(
        val parkourId: ParkourId,
        val parkourAction: ParkourAction
)
