package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.events.data.ParkourActionHistoryRepository
import com.dekitateserver.events.data.ParkourRepository
import com.dekitateserver.events.data.vo.ParkourAction
import com.dekitateserver.events.data.vo.ParkourId
import org.bukkit.entity.Player
import java.time.LocalDateTime

class StartParkourUseCase(
        private val parkourRepository: ParkourRepository,
        private val parkourActionHistoryRepository: ParkourActionHistoryRepository
) {
    suspend operator fun invoke(player: Player, parkourId: ParkourId) {
        val startDateTime = LocalDateTime.now()

        val parkour = parkourRepository.getOrError(parkourId) ?: return

        player.sendMessageIfNotNull(parkour.formattedStartMessage)

        parkourActionHistoryRepository.add(player, parkourId, ParkourAction.START, startDateTime)
    }
}
