package com.dekitateserver.events.domain.usecase.loginbonus

import com.dekitateserver.events.data.LoginBonusRepository

class ReloadLoginBonusUseCase(
        private val loginBonusRepository: LoginBonusRepository
) {
    suspend operator fun invoke() {
        loginBonusRepository.refreshCache()
    }
}
