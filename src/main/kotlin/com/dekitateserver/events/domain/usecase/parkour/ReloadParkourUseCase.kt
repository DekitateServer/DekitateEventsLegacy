package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.data.ParkourRepository

class ReloadParkourUseCase(
        private val parkourRepository: ParkourRepository
) {
    suspend operator fun invoke() {
        parkourRepository.refreshCache()
    }
}
