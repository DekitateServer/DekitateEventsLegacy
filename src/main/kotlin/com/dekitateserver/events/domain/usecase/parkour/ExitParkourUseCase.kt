package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.core.util.teleportIfNotNull
import com.dekitateserver.events.data.ParkourActionHistoryRepository
import com.dekitateserver.events.data.ParkourRepository
import com.dekitateserver.events.data.vo.ParkourAction
import com.dekitateserver.events.data.vo.ParkourId
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.LocalDateTime

class ExitParkourUseCase(
        private val parkourRepository: ParkourRepository,
        private val parkourActionHistoryRepository: ParkourActionHistoryRepository
) {
    suspend operator fun invoke(player: Player, parkourId: ParkourId): ExitParkourUseCaseResult? {
        val parkour = parkourRepository.getOrError(parkourId) ?: return null

        player.teleportIfNotNull(parkour.exitLocation)
        val exitDateTime = LocalDateTime.now()

        player.sendMessageIfNotNull(parkour.formattedExitMessage)

        parkourActionHistoryRepository.add(player, parkourId, ParkourAction.EXIT, exitDateTime)

        return ExitParkourUseCaseResult(
                spawnLocation = parkour.exitLocation
        )
    }
}

data class ExitParkourUseCaseResult(
        val spawnLocation: Location?
)
