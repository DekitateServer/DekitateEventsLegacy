package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.core.util.broadcastMessageWithoutMe
import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.core.util.teleportIfNotNull
import com.dekitateserver.events.data.ParkourActionHistoryRepository
import com.dekitateserver.events.data.ParkourRepository
import com.dekitateserver.events.data.vo.ParkourAction
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.util.Log
import org.apache.commons.lang.time.DurationFormatUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.Duration
import java.time.LocalDateTime

class EndParkourUseCase(
        private val parkourRepository: ParkourRepository,
        private val parkourActionHistoryRepository: ParkourActionHistoryRepository
) {
    suspend operator fun invoke(player: Player, parkourId: ParkourId): EndParkourUseCaseResult? {
        val endDateTime = LocalDateTime.now()

        val parkour = parkourRepository.getOrError(parkourId) ?: return null

        player.teleportIfNotNull(parkour.exitLocation)

        // calc clear time
        val startDateTime = parkourActionHistoryRepository.getLatestActionedDateTime(player, parkourId, ParkourAction.START)

        val time = if (startDateTime != null) {
            val duration = Duration.between(startDateTime, endDateTime)
            DurationFormatUtils.formatDuration(duration.toMillis(), "mm:ss.SSS")
        } else {
            Log.warn("${player.name}のParkour(${parkourId.value})スタート時間取得に失敗.")
            "unknown"
        }

        player.sendMessageIfNotNull(parkour.formattedEndMessage?.replace("{time}", time))

        if (parkour.formattedEndBroadcastMessage != null) {
            val message = parkour.formattedEndBroadcastMessage
                    .replace("{player}", player.name)
                    .replace("{username}", player.displayName)
                    .replace("{time}", time)

            player.broadcastMessageWithoutMe(message)
        }

        Log.info("${player.name}がParkour(${parkourId.value})をクリア.")

        parkourActionHistoryRepository.add(player, parkourId, ParkourAction.END, endDateTime)

        return EndParkourUseCaseResult(
                spawnLocation = parkour.exitLocation,
                eventTicketAmount = parkour.rewardEventTicketAmount
        )
    }
}

data class EndParkourUseCaseResult(
        val spawnLocation: Location?,
        val eventTicketAmount: Int
)
