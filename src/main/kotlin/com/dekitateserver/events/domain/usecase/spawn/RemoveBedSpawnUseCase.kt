package com.dekitateserver.events.domain.usecase.spawn

import org.bukkit.entity.Player

class RemoveBedSpawnUseCase {

    companion object {
        private const val MESSAGE_BED_SPAWN_REMOVED = "§eセーブポイントが自動的に消去されました."
    }

    operator fun invoke(player: Player) {
        player.setBedSpawnLocation(null, true)
        player.sendMessage(MESSAGE_BED_SPAWN_REMOVED)
    }
}
