package com.dekitateserver.events.domain.usecase.eventticket

import com.dekitateserver.events.data.EventTicketHistoryRepository
import com.dekitateserver.events.data.entity.EventTicket
import com.dekitateserver.events.util.Log
import com.dekitateserver.events.util.addItemOrDrop
import org.bukkit.entity.Player
import kotlin.math.min

class GiveEventTicketUseCase(
        private val eventTicketHistoryRepository: EventTicketHistoryRepository
) {
    companion object {
        private const val LIMIT_PER_DAY = 5
        private const val MESSAGE_DAY_LIMIT_OVER = "${EventTicket.DISPLAY_NAME}§7は, §c1日${LIMIT_PER_DAY}枚まで§7です."
    }

    suspend operator fun invoke(player: Player, amount: Int) {
        if (amount < 0) {
            Log.error("1枚以上を指定してください.")
            return
        }

        val gaveAmountToday = eventTicketHistoryRepository.getGaveAmountToday(player)
        if (gaveAmountToday < 0) {
            Log.error("イベントチケット枚数の取得に失敗しました.")
            return
        }

        val giveAmount = min(amount, LIMIT_PER_DAY - gaveAmountToday)
        if (giveAmount <= 0) {
            player.sendMessage(MESSAGE_DAY_LIMIT_OVER)
            return
        }

        player.addItemOrDrop(EventTicket(giveAmount))

        eventTicketHistoryRepository.add(player, giveAmount)
    }
}
