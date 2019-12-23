package com.dekitateserver.events.domain.usecase.spawn

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player

class SetSpawnUseCase {
    operator fun invoke(player: Player, location: Location, isSilent: Boolean = false) {
        player.setBedSpawnLocation(location, true)

        if (!isSilent) {
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.1f)
            player.sendMessage("§bセーブしました！")
        }
    }
}
