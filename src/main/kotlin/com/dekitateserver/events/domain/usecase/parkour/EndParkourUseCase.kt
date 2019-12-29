package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.core.util.broadcastMessageWithoutMe
import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.EventTicketHistoryRepository
import com.dekitateserver.events.domain.repository.ParkourActionHistoryRepository
import com.dekitateserver.events.domain.repository.ParkourRepository
import com.dekitateserver.events.domain.usecase.eventticket.GiveEventTicketUseCase
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourId
import com.dekitateserver.events.util.Log
import org.apache.commons.lang.time.DurationFormatUtils
import org.bukkit.entity.Player
import java.time.Duration
import java.time.LocalDateTime

class EndParkourUseCase(
        private val parkourRepository: ParkourRepository,
        private val parkourActionHistoryRepository: ParkourActionHistoryRepository,
        eventTicketHistoryRepository: EventTicketHistoryRepository
) {
    private val setSpawnUseCase = SetSpawnUseCase()

    private val giveEventTicketUseCase = GiveEventTicketUseCase(eventTicketHistoryRepository)

    suspend operator fun invoke(player: Player, parkourId: ParkourId) {
        val endDateTime = LocalDateTime.now()

        val parkour = parkourRepository.getOrError(parkourId) ?: return

        if (parkour.exitLocation != null) {
            player.teleport(parkour.exitLocation)

            setSpawnUseCase(
                    player = player,
                    location = parkour.exitLocation
            )
        }

        // calc start-end time
        val startDateTime = parkourActionHistoryRepository.getLatestActionedDateTime(player, parkourId, ParkourAction.START)

        val time = if (startDateTime != null) {
            val duration = Duration.between(startDateTime, endDateTime)
            DurationFormatUtils.formatDuration(duration.toMillis(), "mm:ss.SSS")
        } else {
            Log.warn("${player.name}のParkour(${parkourId.value})スタート時間取得に失敗")
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

        if (parkour.hasEventTicketReward) {
            giveEventTicketUseCase(
                    player = player,
                    amount = parkour.rewardEventTicketAmount
            )
        }

        Log.info("${player.name}がParkour(${parkourId.value})をクリア")

        parkourActionHistoryRepository.add(player, parkourId, ParkourAction.END, endDateTime)
    }
}
