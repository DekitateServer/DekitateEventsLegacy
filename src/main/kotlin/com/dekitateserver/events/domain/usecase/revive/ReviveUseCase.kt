package com.dekitateserver.events.domain.usecase.revive

import com.dekitateserver.core.util.getExperience
import com.dekitateserver.core.util.takeExperience
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player

class ReviveUseCase {
    operator fun invoke(player: Player, location: Location, experience: Int) {
        if (experience < 1) {
            Log.error("復活経験値が1未満です [experience: $experience]")
            return
        }

        val diffExp = experience - player.getExperience()

        if (diffExp > 0) {
            player.sendMessage("§e復活に必要な経験値が ${diffExp}Exp 足りません")
            return
        }

        player.takeExperience(experience)

        player.teleport(location)
        player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.0f)

        player.spawnParticle(Particle.VILLAGER_HAPPY, location, 100, 0.6, 0.6, 0.6)
    }
}
