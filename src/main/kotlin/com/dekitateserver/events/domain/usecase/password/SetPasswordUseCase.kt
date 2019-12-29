package com.dekitateserver.events.domain.usecase.password

import com.dekitateserver.events.data.PasswordRepository
import com.dekitateserver.events.domain.vo.PasswordId
import com.dekitateserver.events.util.Log

class SetPasswordUseCase(
        private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke(passwordId: PasswordId, text: String) {
        val password = passwordRepository.getOrError(passwordId) ?: return

        password.set(text)

        Log.info("Password(${passwordId.value})を'$text'に設定しました")

        passwordRepository.update(password)
    }
}
