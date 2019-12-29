package com.dekitateserver.events.domain.usecase.gacha

import com.dekitateserver.events.domain.entity.Gacha
import com.dekitateserver.events.domain.repository.GachaRepository
import com.dekitateserver.events.domain.vo.GachaId
import com.dekitateserver.events.util.sendGachaIdNotFound
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class DemoGachaUseCase(
        private val gachaRepository: GachaRepository
) {
    operator fun invoke(sender: CommandSender, gachaId: GachaId, times: Int) {
        val gacha = gachaRepository.get(gachaId) ?: let {
            sender.sendGachaIdNotFound(gachaId)
            return
        }

        if (times !in 1..1000000) {
            sender.sendWarnMessage("回数は1-1000000の範囲で入力してください")
            return
        }

        val resultMap = mutableMapOf<Gacha.Item, Int>()

        for (i in 0 until times) {
            val key = gacha.getItemByRandom()
            val count = resultMap[key]

            if (count != null) {
                resultMap.replace(key, count + 1)
            } else {
                resultMap[key] = 1
            }
        }

        val messageList = mutableListOf("§7--------- §dGachaデモ(${times}回) §7---------")

        resultMap.map { (item, count) ->
            messageList.add("${item.name}§r: ${count}回")
        }

        sender.sendMessage(messageList.toTypedArray())
    }
}
