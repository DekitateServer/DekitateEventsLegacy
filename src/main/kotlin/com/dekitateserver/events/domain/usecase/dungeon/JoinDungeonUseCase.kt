package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.core.util.broadcastMessageWithoutMe
import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.core.util.teleportIfNotNull
import com.dekitateserver.events.data.DungeonRepository
import com.dekitateserver.events.data.vo.DungeonAction
import com.dekitateserver.events.data.vo.DungeonActionHistoryRepository
import com.dekitateserver.events.data.vo.DungeonId
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.LocalDateTime

class JoinDungeonUseCase(
        private val dungeonRepository: DungeonRepository,
        private val dungeonActionHistoryRepository: DungeonActionHistoryRepository
) {
    suspend operator fun invoke(player: Player, dungeonId: DungeonId): JoinDungeonUseCaseResult? {
        val dungeon = dungeonRepository.getOrError(dungeonId) ?: return null

        player.teleportIfNotNull(dungeon.joinLocation)
        val joinDateTime = LocalDateTime.now()

        player.sendMessageIfNotNull(dungeon.formattedJoinMessage)

        if (dungeon.formattedJoinBroadcastMessage != null) {
            val message = dungeon.formattedJoinBroadcastMessage
                    .replace("{player}", player.name)
                    .replace("{username}", player.displayName)

            player.broadcastMessageWithoutMe(message)
        }

        Log.info("${player.name}がDungeon(${dungeonId.value})に参加")

        dungeonActionHistoryRepository.add(player, dungeonId, DungeonAction.JOIN, joinDateTime)

        return JoinDungeonUseCaseResult(
                spawnLocation = dungeon.joinLocation
        )
    }
}

data class JoinDungeonUseCaseResult(
        val spawnLocation: Location?
)
