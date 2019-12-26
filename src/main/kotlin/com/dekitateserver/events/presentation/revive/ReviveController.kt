package com.dekitateserver.events.presentation.revive

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.revive.CreateReviveSignUseCase
import com.dekitateserver.events.util.sendWarnMessage
import com.dekitateserver.events.util.toDoubleOrError
import com.dekitateserver.events.util.toIntOrError
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.SignChangeEvent

class ReviveController(plugin: DekitateEventsPlugin) {

    private val createReviveSignUseCase = CreateReviveSignUseCase(plugin.signMetaRepository)

    fun clickSign(player: Player, location: Location) {

    }

    fun createSign(event: SignChangeEvent) {
        val player = event.player

        val xyz = event.getLine(1).orEmpty().split(" ")
        if (xyz.size < 3) {
            player.sendWarnMessage("座標パラメータが不足しています.")
            return
        }

        val createReviveSignUseCaseResult = createReviveSignUseCase(
                location = event.block.location,
                player = event.player,
                x = xyz[0].toDoubleOrError() ?: return,
                y = xyz[1].toDoubleOrError() ?: return,
                z = xyz[2].toDoubleOrError() ?: return,
                experience = event.getLine(2).orEmpty().toIntOrError() ?: return

        ) ?: return

        createReviveSignUseCaseResult.signLines.apply(event)
    }
}
