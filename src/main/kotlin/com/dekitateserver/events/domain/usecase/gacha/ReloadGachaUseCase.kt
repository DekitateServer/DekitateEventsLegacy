package com.dekitateserver.events.domain.usecase.gacha

import com.dekitateserver.events.data.GachaRepository

class ReloadGachaUseCase(
        private val gachaRepository: GachaRepository
) {
    suspend operator fun invoke() {
        gachaRepository.refreshCache()
    }
}
