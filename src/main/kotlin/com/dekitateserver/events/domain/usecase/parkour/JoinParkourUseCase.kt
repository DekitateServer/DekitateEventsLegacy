package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.core.util.broadcastMessageWithoutMe
import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.core.util.teleportIfNotNull
import com.dekitateserver.events.data.ParkourActionHistoryRepository
import com.dekitateserver.events.data.ParkourRepository
import com.dekitateserver.events.data.vo.ParkourAction
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.LocalDateTime

class JoinParkourUseCase(
        private val parkourRepository: ParkourRepository,
        private val parkourActionHistoryRepository: ParkourActionHistoryRepository
) {
    suspend operator fun invoke(player: Player, parkourId: ParkourId): JoinParkourUseCaseResult? {
        val parkour = parkourRepository.getOrError(parkourId) ?: return null

        player.teleportIfNotNull(parkour.joinLocation)
        val joinDateTime = LocalDateTime.now()

        player.sendMessageIfNotNull(parkour.formattedJoinMessage)

        if (parkour.formattedJoinBroadcastMessage != null) {
            val message = parkour.formattedJoinBroadcastMessage
                    .replace("{player}", player.name)
                    .replace("{username}", player.displayName)

            player.broadcastMessageWithoutMe(message)
        }

        Log.info("${player.name}がParkour(${parkourId.value})に参加.")

        parkourActionHistoryRepository.add(player, parkourId, ParkourAction.JOIN, joinDateTime)

        return JoinParkourUseCaseResult(
                spawnLocation = parkour.joinLocation
        )
    }
}

data class JoinParkourUseCaseResult(
        val spawnLocation: Location?
)
