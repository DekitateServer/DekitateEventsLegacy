package com.dekitateserver.events.domain.usecase.key

import com.dekitateserver.events.data.KeyRepository

class ReloadKeyUseCase(
        private val keyRepository: KeyRepository
) {
    suspend operator fun invoke() {
        keyRepository.refreshCache()
    }
}
