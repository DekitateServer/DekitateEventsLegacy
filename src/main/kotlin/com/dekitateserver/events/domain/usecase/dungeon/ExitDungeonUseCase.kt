package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.core.bukkit.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.DungeonActionHistoryRepository
import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.vo.DungeonAction
import com.dekitateserver.events.domain.vo.DungeonId
import org.bukkit.entity.Player
import java.time.LocalDateTime

class ExitDungeonUseCase(
        private val dungeonRepository: DungeonRepository,
        private val dungeonActionHistoryRepository: DungeonActionHistoryRepository
) {
    private val setSpawnUseCase = SetSpawnUseCase()

    suspend operator fun invoke(player: Player, dungeonId: DungeonId) {
        val dungeon = dungeonRepository.getOrError(dungeonId) ?: return

        val exitLocation = dungeon.exitLocation
        if (exitLocation != null) {
            player.teleport(exitLocation)
            setSpawnUseCase(player, exitLocation)
        }

        val exitDateTime = LocalDateTime.now()

        player.sendMessageIfNotNull(dungeon.formattedExitMessage)

        dungeonActionHistoryRepository.add(player, dungeonId, DungeonAction.EXIT, exitDateTime)
    }
}
