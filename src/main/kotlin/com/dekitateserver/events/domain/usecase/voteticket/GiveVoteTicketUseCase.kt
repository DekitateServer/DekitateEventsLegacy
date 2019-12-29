package com.dekitateserver.events.domain.usecase.voteticket

import com.dekitateserver.events.domain.entity.VoteTicket
import com.dekitateserver.events.domain.repository.VoteTicketHistoryRepository
import com.dekitateserver.events.util.Log
import com.dekitateserver.events.util.addItemOrDrop
import org.bukkit.entity.Player
import kotlin.math.min

class GiveVoteTicketUseCase(
        private val voteTicketHistoryRepository: VoteTicketHistoryRepository
) {
    companion object {
        private const val LIMIT_PER_DAY = 2
        private const val MESSAGE_DAY_LIMIT_OVER = "${VoteTicket.DISPLAY_NAME}§7は§c1日${LIMIT_PER_DAY}枚まで§7です"
    }

    suspend operator fun invoke(player: Player, amount: Int) {
        if (amount < 0) {
            Log.error("1枚以上を指定してください")
            return
        }

        val gaveAmountToday = voteTicketHistoryRepository.getGaveAmountToday(player)
        if (gaveAmountToday < 0) {
            Log.error("投票チケット枚数の取得に失敗しました")
            return
        }

        val giveAmount = min(amount, LIMIT_PER_DAY - gaveAmountToday)
        if (giveAmount <= 0) {
            player.sendMessage(MESSAGE_DAY_LIMIT_OVER)
            return
        }

        player.addItemOrDrop(VoteTicket(player, giveAmount))

        voteTicketHistoryRepository.add(player, giveAmount)
    }
}
