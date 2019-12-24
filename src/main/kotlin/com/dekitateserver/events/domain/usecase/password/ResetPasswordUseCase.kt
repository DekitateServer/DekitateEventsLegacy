package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.events.data.PasswordRepository
import com.dekitateserver.events.data.vo.PasswordId
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
