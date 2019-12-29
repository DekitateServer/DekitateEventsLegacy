package com.dekitateserver.events.domain.usecase.gacha

import com.dekitateserver.events.domain.repository.GachaRepository
import com.dekitateserver.events.domain.vo.GachaId
import com.dekitateserver.events.util.sendGachaIdNotFound
import org.bukkit.command.CommandSender

class SendGachaInfoUseCase(
        private val gachaRepository: GachaRepository
) {
    operator fun invoke(sender: CommandSender, gachaId: GachaId) {
        val gacha = gachaRepository.get(gachaId) ?: let {
            sender.sendGachaIdNotFound(gachaId)
            return
        }

        val messages = arrayOf(
                "§7--------- §dGacha §7---------",
                " ID: ${gachaId.value}",
                " Name: ${gacha.name}",
                " ItemSize: ${gacha.itemSize}",
                " WinMessage: ${gacha.winMessage}",
                " LoseMessage: ${gacha.loseMessage}",
                " WinBroadcastMessage: ${gacha.winBroadcastMessage}",
                " EnabledEffect: ${gacha.isEnabledEffect}")

        sender.sendMessage(messages)
    }
}
