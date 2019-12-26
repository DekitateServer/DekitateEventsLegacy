package com.dekitateserver.events.domain.usecase.spawn

import com.dekitateserver.events.data.SignMetaRepository
import com.dekitateserver.events.data.vo.SignLines
import com.dekitateserver.events.presentation.spawn.SpawnSignContracts
import com.dekitateserver.events.util.sendSuccessMessage
import kotlinx.coroutines.runBlocking
import org.bukkit.Location
import org.bukkit.entity.Player

class CreateSpawnSignUseCase(
        private val signMetaRepository: SignMetaRepository
) : AbstractSpawnSignUseCase() {

    operator fun invoke(location: Location, player: Player, x: Double, y: Double, z: Double, comment: String?): CreateSpawnSignUseCaseResult? {
        val signMeta = signMetaRepository.getOrNew(location).apply {
            put(KEY_SIGN_META_SETSPAWN_LOCATION, Location(location.world, x, y, z))
        }

        val isAddSuccessful = runBlocking {
            signMetaRepository.add(signMeta)
        }

        return if (isAddSuccessful) {
            player.sendSuccessMessage("看板を作成しました")

            val signLines = SignLines(
                    line0 = SpawnSignContracts.SIGN_INDEX,
                    line1 = "",
                    line2 = comment ?: "",
                    line3 = ""
            )

            CreateSpawnSignUseCaseResult(signLines)
        } else null
    }
}

data class CreateSpawnSignUseCaseResult(
        val signLines: SignLines
)
