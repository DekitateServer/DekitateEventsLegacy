package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.domain.repository.PasswordRepository

class ReloadPasswordUseCase(
        private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke() {
        passwordRepository.refreshCache()
    }
}
