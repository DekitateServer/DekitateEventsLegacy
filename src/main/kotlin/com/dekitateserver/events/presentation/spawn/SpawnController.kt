package com.dekitateserver.events.presentation.spawn

import com.dekitateserver.events.DekitateEvents
import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.spawn.CreateSpawnSignUseCase
import com.dekitateserver.events.domain.usecase.spawn.GetSetSpawnSignUseCase
import com.dekitateserver.events.domain.usecase.spawn.RemoveBedSpawnUseCase
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.util.sendWarnMessage
import com.dekitateserver.events.util.toDoubleOrError
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.SignChangeEvent

class SpawnController(plugin: DekitateEventsPlugin) {

    private val setSpawnUseCase = SetSpawnUseCase()
    private val removeBedSpawnUseCase = RemoveBedSpawnUseCase()
    private val getSetSpawnSignUseCase = GetSetSpawnSignUseCase(plugin.signMetaRepository)
    private val createSpawnSignUseCase = CreateSpawnSignUseCase(plugin.signMetaRepository)

    fun removeBedSpawnInEvents(player: Player) {
        if (player.bedSpawnLocation?.world?.name != DekitateEvents.NAME_EVENT_WORLD) {
            return
        }

        removeBedSpawnUseCase(player)
    }

    fun clickSign(player: Player, location: Location) {
        val setspawnLocation = getSetSpawnSignUseCase(location) ?: return
        setSpawnUseCase(player, setspawnLocation)
    }

    fun createSign(event: SignChangeEvent) {
        val player = event.player

        val xyz = event.getLine(1).orEmpty().split(" ")
        if (xyz.size < 3) {
            player.sendWarnMessage("座標パラメータが不足しています.")
            return
        }

        val signLines = createSpawnSignUseCase(
                location = event.block.location,
                player = event.player,
                x = xyz[0].toDoubleOrError() ?: return,
                y = xyz[1].toDoubleOrError() ?: return,
                z = xyz[2].toDoubleOrError() ?: return,
                comment = event.getLine(2)

        ) ?: return

        signLines.apply(event)
    }
}
