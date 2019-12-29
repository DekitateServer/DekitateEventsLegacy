package com.dekitateserver.events.domain.entity

import com.dekitateserver.events.domain.vo.LoginBonusId
import java.time.LocalDateTime

data class LoginBonus(
        val id: LoginBonusId,
        val start: LocalDateTime,
        val end: LocalDateTime,
        val command: String,
        val message: String?
)
