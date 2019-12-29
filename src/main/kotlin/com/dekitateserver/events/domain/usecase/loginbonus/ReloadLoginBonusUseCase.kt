package com.dekitateserver.events.domain.usecase.loginbonus

import com.dekitateserver.events.domain.repository.LoginBonusRepository

class ReloadLoginBonusUseCase(
        private val loginBonusRepository: LoginBonusRepository
) {
    suspend operator fun invoke() {
        loginBonusRepository.refreshCache()
    }
}
