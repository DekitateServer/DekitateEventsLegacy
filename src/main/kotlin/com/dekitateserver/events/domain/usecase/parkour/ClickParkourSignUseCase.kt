package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.domain.repository.EventTicketHistoryRepository
import com.dekitateserver.events.domain.repository.ParkourActionHistoryRepository
import com.dekitateserver.events.domain.repository.ParkourRepository
import com.dekitateserver.events.domain.repository.SignMetaRepository
import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourId
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.entity.Player

class ClickParkourSignUseCase(
        parkourRepository: ParkourRepository,
        private val signMetaRepository: SignMetaRepository,
        parkourActionHistoryRepository: ParkourActionHistoryRepository,
        eventTicketHistoryRepository: EventTicketHistoryRepository
) : AbstractParkourSignUseCase() {

    private val joinParkourUseCase = JoinParkourUseCase(parkourRepository, parkourActionHistoryRepository)
    private val endParkourUseCase = EndParkourUseCase(parkourRepository, parkourActionHistoryRepository, eventTicketHistoryRepository)
    private val exitParkourUseCase = ExitParkourUseCase(parkourRepository, parkourActionHistoryRepository)

    suspend operator fun invoke(player: Player, location: Location) {
        val signMeta = signMetaRepository.getOrError(location) ?: return

        val parkourIdString = signMeta.getStringOrError(KEY_SIGN_META_PARKOUR_ID) ?: return
        val parkourActionString = signMeta.getStringOrError(KEY_SIGN_META_PARKOUR_ACTION) ?: return

        val parkourId = ParkourId(parkourIdString)

        when (ParkourAction.valueOf(parkourActionString)) {
            ParkourAction.JOIN -> joinParkourUseCase(player, parkourId)
            ParkourAction.END -> endParkourUseCase(player, parkourId)
            ParkourAction.EXIT -> exitParkourUseCase(player, parkourId)
            else -> Log.error("非対応のParkourAction($parkourActionString)です.")
        }
    }
}
