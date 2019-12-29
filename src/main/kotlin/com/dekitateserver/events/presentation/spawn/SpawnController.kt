package com.dekitateserver.events.presentation.spawn

import com.dekitateserver.events.DekitateEvents
import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.usecase.spawn.CreateSpawnSignUseCase
import com.dekitateserver.events.domain.usecase.spawn.GetSetSpawnSignUseCase
import com.dekitateserver.events.domain.usecase.spawn.RemoveBedSpawnUseCase
import com.dekitateserver.events.domain.usecase.spawn.SetSpawnUseCase
import com.dekitateserver.events.domain.vo.SignLines
import com.dekitateserver.events.util.sendWarnMessage
import com.dekitateserver.events.util.toDoubleOrError
import org.bukkit.Location
import org.bukkit.entity.Player

class SpawnController(plugin: DekitateEventsPlugin) {

    private val setSpawnUseCase = SetSpawnUseCase()
    private val removeBedSpawnUseCase = RemoveBedSpawnUseCase()
    private val getSetSpawnSignUseCase = GetSetSpawnSignUseCase(plugin.signMetaRepository)
    private val createSpawnSignUseCase = CreateSpawnSignUseCase(plugin.signMetaRepository)

    fun removeBedSpawnIfInEvents(player: Player) {
        if (player.bedSpawnLocation?.world?.name != DekitateEvents.NAME_EVENT_WORLD) {
            return
        }

        removeBedSpawnUseCase(player)
    }

    fun clickSign(player: Player, location: Location) {
        val getSetSpawnSignUseCaseResult = getSetSpawnSignUseCase(location) ?: return

        setSpawnUseCase(
                player = player,
                location = getSetSpawnSignUseCaseResult.location
        )
    }

    fun createSign(player: Player, location: Location, argLocation: String, argComment: String): SignLines? {
        val xyz = argLocation.split(" ")
        if (xyz.size < 3) {
            player.sendWarnMessage("座標パラメータが不足しています")
            return null
        }

        val createSpawnSignUseCaseResult = createSpawnSignUseCase(
                location = location,
                player = player,
                x = xyz[0].toDoubleOrError() ?: return null,
                y = xyz[1].toDoubleOrError() ?: return null,
                z = xyz[2].toDoubleOrError() ?: return null,
                comment = argComment

        ) ?: return null

        return createSpawnSignUseCaseResult.signLines
    }
}
