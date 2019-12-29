package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.data.SignMetaRepository
import com.dekitateserver.events.domain.repository.ParkourRepository
import com.dekitateserver.events.domain.vo.ParkourAction
import com.dekitateserver.events.domain.vo.ParkourId
import com.dekitateserver.events.domain.vo.SignLines
import com.dekitateserver.events.presentation.parkour.ParkourSignContracts
import com.dekitateserver.events.util.sendParkourIdNotFound
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import kotlinx.coroutines.runBlocking
import org.bukkit.Location
import org.bukkit.entity.Player

class CreateParkourSignUseCase(
        private val parkourRepository: ParkourRepository,
        private val signMetaRepository: SignMetaRepository
) : AbstractParkourSignUseCase() {

    operator fun invoke(location: Location, player: Player, parkourId: ParkourId, action: ParkourAction): CreateParkourSignUseCaseResult? {
        val parkour = parkourRepository.get(parkourId) ?: let {
            player.sendParkourIdNotFound(parkourId)
            return null
        }

        val signMeta = signMetaRepository.getOrNew(location).apply {
            put(KEY_SIGN_META_PARKOUR_ID, parkourId.value)
            put(KEY_SIGN_META_PARKOUR_ACTION, action.name)
        }

        val actionLine: String = when (action) {
            ParkourAction.JOIN -> "§c§n参加"
            ParkourAction.END -> "§b§nクリア"
            ParkourAction.EXIT -> "§1§n退出"
            ParkourAction.START -> {
                player.sendWarnMessage("ParkourAction($action)は看板に非対応です")
                return null
            }
        }

        val isAddSuccessful = runBlocking {
            signMetaRepository.add(signMeta)
        }

        return if (isAddSuccessful) {
            player.sendSuccessMessage("看板を作成しました")

            val signLines = SignLines(
                    line0 = ParkourSignContracts.SIGN_INDEX,
                    line1 = parkour.name,
                    line2 = "",
                    line3 = actionLine
            )

            CreateParkourSignUseCaseResult(signLines)
        } else null
    }
}

data class CreateParkourSignUseCaseResult(
        val signLines: SignLines
)
