package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.core.bukkit.util.sendMessageIfNotNull
import com.dekitateserver.events.domain.repository.PasswordRepository
import com.dekitateserver.events.domain.vo.PasswordId
import org.bukkit.entity.Player

class ResetPasswordUseCase(
        private val passwordRepository: PasswordRepository
) {
    operator fun invoke(player: Player, passwordId: PasswordId) {
        val password = passwordRepository.getOrError(passwordId) ?: return

        password.reset()

        player.sendMessageIfNotNull(password.resetMessage)
    }
}
