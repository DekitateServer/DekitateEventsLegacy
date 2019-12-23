package com.dekitateserver.events.domain.usecase.gacha

import com.dekitateserver.core.util.broadcastMessageWithoutMe
import com.dekitateserver.core.util.sendMessageIfNotNull
import com.dekitateserver.events.data.GachaHistoryRepository
import com.dekitateserver.events.data.GachaRepository
import com.dekitateserver.events.data.entity.Gacha
import com.dekitateserver.events.data.vo.GachaId
import com.dekitateserver.events.util.Log
import org.bukkit.Server
import org.bukkit.Sound
import org.bukkit.entity.Player

class PlayGachaUseCase(
        private val server: Server,
        private val gachaRepository: GachaRepository,
        private val gachaHistoryRepository: GachaHistoryRepository
) {
    companion object {
        private val WIN_SOUND = Sound.ENTITY_PLAYER_LEVELUP
        private val LOSE_SOUND = Sound.ENTITY_PIG_DEATH
    }

    private val consoleCommandSender = server.consoleSender

    suspend operator fun invoke(player: Player, gachaId: GachaId) {
        val gacha = gachaRepository.getOrError(gachaId) ?: return

        val item = gacha.getItemByRandom()

        val isWin = if (item.isEnabledOncePerDay) {
            item.isWin && !gachaHistoryRepository.hasToday(gachaId, item.id)
        } else {
            item.isWin
        }

        val sound: Sound
        if (isWin) {
            item.dispatchCommands(player)

            player.sendMessageIfNotNull(gacha.winMessage?.replace("{name}", item.name))
            player.broadcastWinMessageIfNeeded(gacha, item)

            sound = WIN_SOUND

            Log.info("Gacha(${gacha.id.value})の'${item.name}'を${player.name}に実行しました.")

            gachaHistoryRepository.add(player, gachaId, item.id)
        } else {
            player.sendMessageIfNotNull(gacha.loseMessage)
            sound = LOSE_SOUND
        }

        if (gacha.isEnabledEffect) {
            player.playSound(player.location, sound, 1f, 0f)
        }
    }

    private fun Gacha.Item.dispatchCommands(player: Player) {
        commandList.forEach {
            server.dispatchCommand(consoleCommandSender, it.replace("{player}", player.name))
        }
    }

    private fun Player.broadcastWinMessageIfNeeded(gacha: Gacha, item: Gacha.Item) {
        if (gacha.winBroadcastMessage == null || item.isDisabledBroadcast) {
            return
        }

        val message = gacha.winBroadcastMessage.replace("{player}", name)
                .replace("{username}", displayName)
                .replace("{name}", item.name)

        broadcastMessageWithoutMe(message)
    }
}
