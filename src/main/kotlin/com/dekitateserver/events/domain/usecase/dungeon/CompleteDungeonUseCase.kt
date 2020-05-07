package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.core.bukkit.util.broadcastMessageWithoutMe
import com.dekitateserver.core.bukkit.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.*
import com.dekitateserver.events.domain.usecase.eventticket.GiveEventTicketUseCase
import com.dekitateserver.events.domain.usecase.gacha.PlayGachaUseCase
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.vo.DungeonAction
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.lang.time.DurationFormatUtils
import org.bukkit.Server
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.time.Duration
import java.time.LocalDateTime

class CompleteDungeonUseCase(
        server: Server,
        private val gachaScope: CoroutineScope,
        private val dungeonRepository: DungeonRepository,
        private val dungeonActionHistoryRepository: DungeonActionHistoryRepository,
        gachaRepository: GachaRepository,
        gachaHistoryRepository: GachaHistoryRepository,
        eventTicketHistoryRepository: EventTicketHistoryRepository
) {
    companion object {
        private const val DELAY_GACHA_MILLIS = 10000L
    }

    private val setSpawnUseCase = SetSpawnUseCase()

    private val giveEventTicketUseCase = GiveEventTicketUseCase(eventTicketHistoryRepository)

    private val playGachaUseCase = PlayGachaUseCase(server, gachaRepository, gachaHistoryRepository)

    suspend operator fun invoke(player: Player, dungeonId: DungeonId) {
        val completeDateTime = LocalDateTime.now()

        val dungeon = dungeonRepository.getOrError(dungeonId) ?: return

        val completeLocation = dungeon.completeLocation
        if (completeLocation != null) {
            player.teleport(completeLocation)
            setSpawnUseCase(player, completeLocation)
        }

        if (dungeon.isEnabledCompleteSound) {
            player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.2f, 0.0f)
        }

        // calc join-complete time
        val joinDateTime = dungeonActionHistoryRepository.getLatestActionedDateTime(player, dungeonId, DungeonAction.JOIN)

        val time = if (joinDateTime != null) {
            val duration = Duration.between(joinDateTime, completeDateTime)
            DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss.SSS")
        } else {
            Log.warn("${player.name}のDungeon(${dungeonId.value})参加時間取得に失敗")
            "unknown"
        }

        player.sendMessageIfNotNull(dungeon.formattedCompleteMessage?.replace("{time}", time))

        if (dungeon.formattedCompleteBroadcastMessage != null) {
            val message = dungeon.formattedCompleteBroadcastMessage
                    .replace("{player}", player.name)
                    .replace("{username}", player.displayName)
                    .replace("{time}", time)

            player.broadcastMessageWithoutMe(message)
        }

        Log.info("${player.name}がDungeon(${dungeonId.value})をクリア")

        if (dungeon.hasEventTicketReward) {
            giveEventTicketUseCase(
                    player = player,
                    amount = dungeon.rewardEventTicketAmount
            )
        }

        val gachaId = dungeon.rewardGachaId
        if (gachaId != null) {
            gachaScope.launch {
                delay(DELAY_GACHA_MILLIS)
                playGachaUseCase(player, gachaId)
            }
        }

        dungeonActionHistoryRepository.add(player, dungeonId, DungeonAction.COMPLETE, completeDateTime)
    }
}
