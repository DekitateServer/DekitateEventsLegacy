package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.Log
import org.bukkit.Server

class UnlockDungeonUseCase(
        private val server: Server,
        private val dungeonRepository: DungeonRepository
) {
    suspend operator fun invoke(dungeonId: DungeonId) {
        if (dungeonRepository.unlock(dungeonId)) {
            val dungeon = dungeonRepository.getOrError(dungeonId)

            val unlockBroadcastMessage = dungeon?.formattedUnlockBroadcastMessage
            if (unlockBroadcastMessage != null) {
                server.broadcastMessage(unlockBroadcastMessage)
            }

            Log.info("Dungeon(${dungeonId.value})のロックを解除しました")
        } else {
            Log.error("Dungeon(${dungeonId.value})のロック解除に失敗しました")
        }
    }
}
