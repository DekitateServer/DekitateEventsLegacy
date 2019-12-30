package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.repository.DungeonRepository

class ReloadDungeonUseCase(
        private val dungeonRepository: DungeonRepository
) {
    suspend operator fun invoke() {
        dungeonRepository.refreshCache()
    }
}
