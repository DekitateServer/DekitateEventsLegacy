package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.core.bukkit.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.ParkourActionHistoryRepository
import com.dekitateserver.events.domain.repository.ParkourRepository
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourId
import org.bukkit.entity.Player
import java.time.LocalDateTime

class ExitParkourUseCase(
        private val parkourRepository: ParkourRepository,
        private val parkourActionHistoryRepository: ParkourActionHistoryRepository
) {
    private val setSpawnUseCase = SetSpawnUseCase()

    suspend operator fun invoke(player: Player, parkourId: ParkourId) {
        val parkour = parkourRepository.getOrError(parkourId) ?: return

        val exitLocation = parkour.exitLocation
        if (exitLocation != null) {
            player.teleport(exitLocation)
            setSpawnUseCase(player, exitLocation)
        }

        val exitDateTime = LocalDateTime.now()

        player.sendMessageIfNotNull(parkour.formattedExitMessage)

        parkourActionHistoryRepository.add(player, parkourId, ParkourAction.EXIT, exitDateTime)
    }
}
