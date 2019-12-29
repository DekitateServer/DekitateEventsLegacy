package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.core.util.broadcastMessageWithoutMe
import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.ParkourActionHistoryRepository
import com.dekitateserver.events.domain.repository.ParkourRepository
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourId
import com.dekitateserver.events.util.Log
import org.bukkit.entity.Player
import java.time.LocalDateTime

class JoinParkourUseCase(
        private val parkourRepository: ParkourRepository,
        private val parkourActionHistoryRepository: ParkourActionHistoryRepository
) {
    private val setSpawnUseCase = SetSpawnUseCase()

    suspend operator fun invoke(player: Player, parkourId: ParkourId) {
        val parkour = parkourRepository.getOrError(parkourId) ?: return

        val joinLocation = parkour.joinLocation
        if (joinLocation != null) {
            player.teleport(joinLocation)
            setSpawnUseCase(player, joinLocation)
        }

        val joinDateTime = LocalDateTime.now()

        player.sendMessageIfNotNull(parkour.formattedJoinMessage)

        val joinBroadcastMessage = parkour.formattedJoinBroadcastMessage
        if (joinBroadcastMessage != null) {
            val broadcastMessage = joinBroadcastMessage
                    .replace("{player}", player.name)
                    .replace("{username}", player.displayName)

            player.broadcastMessageWithoutMe(broadcastMessage)
        }

        Log.info("${player.name}がParkour(${parkourId.value})に参加")

        parkourActionHistoryRepository.add(player, parkourId, ParkourAction.JOIN, joinDateTime)
    }
}
