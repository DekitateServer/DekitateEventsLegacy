package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.Log

class LockDungeonUseCase(
        private val dungeonRepository: DungeonRepository
) {
    suspend operator fun invoke(dungeonId: DungeonId, seconds: Long) {
        if (seconds > 0) {
            Log.error("ロック時間は1以上を指定してください [seconds: $seconds]")
            return
        }

        if (dungeonRepository.lock(dungeonId, seconds)) {
            Log.info("Dungeon(${dungeonId.value})を${seconds}秒でロックしました")
        } else {
            Log.error("Dungeon(${dungeonId.value})のロックに失敗しました")
        }
    }
}
