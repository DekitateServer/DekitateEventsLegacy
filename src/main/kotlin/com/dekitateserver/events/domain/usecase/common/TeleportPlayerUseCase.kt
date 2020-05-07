package com.dekitateserver.events.domain.usecase.common

import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.entity.Player

class TeleportPlayerUseCase {

    operator fun invoke(player: Player, location: Location) {
        player.teleport(location)
        Log.info("${player.name}を${location.blockX},${location.blockY},${location.blockZ}にTPしました")
    }
}
