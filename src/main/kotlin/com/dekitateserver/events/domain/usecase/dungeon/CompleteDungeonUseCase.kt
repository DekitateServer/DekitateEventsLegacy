package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.core.util.broadcastMessageWithoutMe
import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.core.util.teleportIfNotNull
import com.dekitateserver.events.data.DungeonRepository
import com.dekitateserver.events.data.vo.DungeonAction
import com.dekitateserver.events.data.vo.DungeonActionHistoryRepository
import com.dekitateserver.events.data.vo.DungeonId
import com.dekitateserver.events.data.vo.GachaId
import com.dekitateserver.events.util.Log
import org.apache.commons.lang.time.DurationFormatUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.Duration
import java.time.LocalDateTime

class CompleteDungeonUseCase(
        private val dungeonRepository: DungeonRepository,
        private val dungeonActionHistoryRepository: DungeonActionHistoryRepository
) {
    suspend operator fun invoke(player: Player, dungeonId: DungeonId): CompleteDungeonUseCaseResult? {
        val completeDateTime = LocalDateTime.now()

        val dungeon = dungeonRepository.getOrError(dungeonId) ?: return null

        player.teleportIfNotNull(dungeon.completeLocation)

        // calc complete time
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

        dungeonActionHistoryRepository.add(player, dungeonId, DungeonAction.COMPLETE, completeDateTime)

        return CompleteDungeonUseCaseResult(
                spawnLocation = dungeon.completeLocation,
                eventTicketAmount = dungeon.rewardEventTicketAmount,
                gachaId = dungeon.rewardGachaId
        )
    }
}

data class CompleteDungeonUseCaseResult(
        val spawnLocation: Location?,
        val eventTicketAmount: Int,
        val gachaId: GachaId?
)
