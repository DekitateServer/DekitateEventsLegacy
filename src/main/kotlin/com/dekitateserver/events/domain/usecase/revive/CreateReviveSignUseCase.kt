package com.dekitateserver.events.domain.usecase.revive

import com.dekitateserver.events.data.SignMetaRepository
import com.dekitateserver.events.data.vo.SignLines
import com.dekitateserver.events.presentation.revive.ReviveSignContracts
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import kotlinx.coroutines.runBlocking
import org.bukkit.Location
import org.bukkit.entity.Player

class CreateReviveSignUseCase(
        private val signMetaRepository: SignMetaRepository
) : AbstractReviveSignUseCase() {

    operator fun invoke(location: Location, player: Player, x: Double, y: Double, z: Double, experience: Int): CreateReviveSignUseCaseResult? {
        if (experience < 1) {
            player.sendWarnMessage("経験値は1以上で指定してください.")
            return null
        }

        val signMeta = signMetaRepository.getOrNew(location).apply {
            put(KEY_SIGN_META_REVIVE_LOCATION, Location(location.world, x, y, z))
            put(KEY_SIGN_META_REVIVE_EXPERIENCE, experience)
        }

        val isAddSuccessful = runBlocking {
            signMetaRepository.add(signMeta)
        }

        return if (isAddSuccessful) {
            player.sendSuccessMessage("看板を作成しました.")

            val signLines = SignLines(
                    line0 = ReviveSignContracts.SIGN_INDEX,
                    line1 = "",
                    line2 = "§l${experience}Exp§r",
                    line3 = ""
            )

            CreateReviveSignUseCaseResult(signLines)
        } else null
    }
}

data class CreateReviveSignUseCaseResult(
        val signLines: SignLines
)
