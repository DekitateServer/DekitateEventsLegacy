package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.core.util.broadcastMessageWithoutMe
import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.DungeonActionHistoryRepository
import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.vo.DungeonAction
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.Log
import org.bukkit.entity.Player
import java.time.LocalDateTime

class JoinDungeonUseCase(
        private val dungeonRepository: DungeonRepository,
        private val dungeonActionHistoryRepository: DungeonActionHistoryRepository
) {
    private val setSpawnUseCase = SetSpawnUseCase()

    suspend operator fun invoke(player: Player, dungeonId: DungeonId) {
        val dungeon = dungeonRepository.getOrError(dungeonId) ?: return

        val joinLocation = dungeon.joinLocation
        if (joinLocation != null) {
            player.teleport(joinLocation)
            setSpawnUseCase(player, joinLocation)
        }

        val joinDateTime = LocalDateTime.now()

        player.sendMessageIfNotNull(dungeon.formattedJoinMessage)

        val joinBroadcastMessage = dungeon.formattedJoinBroadcastMessage
        if (joinBroadcastMessage != null) {
            val message = joinBroadcastMessage
                    .replace("{player}", player.name)
                    .replace("{username}", player.displayName)

            player.broadcastMessageWithoutMe(message)
        }

        Log.info("${player.name}がDungeon(${dungeonId.value})に参加")

        dungeonActionHistoryRepository.add(player, dungeonId, DungeonAction.JOIN, joinDateTime)
    }
}
