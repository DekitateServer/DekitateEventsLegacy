package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.data.PasswordRepository

class ReloadPasswordUseCase(
        private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke() {
        passwordRepository.refreshCache()
    }
}
