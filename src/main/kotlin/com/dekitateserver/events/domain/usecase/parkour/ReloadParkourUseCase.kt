package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.domain.repository.ParkourRepository

class ReloadParkourUseCase(
        private val parkourRepository: ParkourRepository
) {
    suspend operator fun invoke() {
        parkourRepository.refreshCache()
    }
}
