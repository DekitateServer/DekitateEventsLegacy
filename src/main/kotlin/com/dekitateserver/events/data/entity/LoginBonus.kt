package com.dekitateserver.events.data.entity

import com.dekitateserver.events.data.vo.LoginBonusId
import java.time.LocalDateTime

data class LoginBonus(
        val id: LoginBonusId,
        val start: LocalDateTime,
        val end: LocalDateTime,
        val command: String,
        val message: String?
)
