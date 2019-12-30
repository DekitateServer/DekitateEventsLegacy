package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.Log

class UnlockDungeonUseCase(
        private val dungeonRepository: DungeonRepository
) {
    suspend operator fun invoke(dungeonId: DungeonId) {
        if (dungeonRepository.unlock(dungeonId)) {
            Log.info("Dungeon(${dungeonId.value})のロックを解除しました")
        } else {
            Log.error("Dungeon(${dungeonId.value})のロック解除に失敗しました")
        }
    }
}
