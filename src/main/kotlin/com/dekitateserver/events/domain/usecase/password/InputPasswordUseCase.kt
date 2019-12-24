package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.events.data.PasswordRepository
import com.dekitateserver.events.data.vo.PasswordId
import com.dekitateserver.events.data.vo.PasswordInputResult
import org.bukkit.entity.Player

class InputPasswordUseCase(
        private val passwordRepository: PasswordRepository
) {
    operator fun invoke(player: Player, passwordId: PasswordId, text: String) {
        val password = passwordRepository.getOrError(passwordId) ?: return

        val result = password.input(text)
        player.sendMessageIfNotNull(password.inputMessage?.replace("{buff}", password.bufferText))

        val resultMessage = when (result) {
            PasswordInputResult.MATCH -> {
                password.blockMaterialLocation?.let {
                    it.location.block.type = it.material
                }

                password.matchMessage
            }
            PasswordInputResult.NOT_MATCH -> password.notMatchMessage
            PasswordInputResult.CONTINUE -> return
        }

        player.sendMessageIfNotNull(resultMessage)
    }
}
