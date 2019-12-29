package com.dekitateserver.events.presentation.revive

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.revive.CreateReviveSignUseCase
import com.dekitateserver.events.domain.usecase.revive.GetReviveSignUseCase
import com.dekitateserver.events.domain.usecase.revive.ReviveUseCase
import com.dekitateserver.events.domain.vo.SignLines
import com.dekitateserver.events.util.sendWarnMessage
import com.dekitateserver.events.util.toDoubleOrError
import com.dekitateserver.events.util.toIntOrError
import org.bukkit.Location
import org.bukkit.entity.Player

class ReviveController(plugin: DekitateEventsPlugin) {

    private val reviveUseCase = ReviveUseCase()
    private val getReviveSignUseCase = GetReviveSignUseCase(plugin.signMetaRepository)
    private val createReviveSignUseCase = CreateReviveSignUseCase(plugin.signMetaRepository)

    fun clickSign(player: Player, location: Location) {
        val getReviveSignUseCaseResult = getReviveSignUseCase(location) ?: return

        reviveUseCase(
                player = player,
                location = getReviveSignUseCaseResult.location,
                experience = getReviveSignUseCaseResult.experience
        )
    }

    fun createSign(player: Player, location: Location, argLocation: String, arcExperience: String): SignLines? {
        val xyz = argLocation.split(" ")
        if (xyz.size < 3) {
            player.sendWarnMessage("座標パラメータが不足しています")
            return null
        }

        val createReviveSignUseCaseResult = createReviveSignUseCase(
                location = location,
                player = player,
                x = xyz[0].toDoubleOrError() ?: return null,
                y = xyz[1].toDoubleOrError() ?: return null,
                z = xyz[2].toDoubleOrError() ?: return null,
                experience = arcExperience.toIntOrError() ?: return null

        ) ?: return null

        return createReviveSignUseCaseResult.signLines
    }
}
