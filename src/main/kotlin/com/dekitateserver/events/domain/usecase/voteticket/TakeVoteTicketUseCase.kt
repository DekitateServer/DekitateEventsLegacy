package com.dekitateserver.events.domain.usecase.voteticket

import com.dekitateserver.events.domain.entity.VoteTicket
import com.dekitateserver.events.domain.repository.VoteTicketHistoryRepository
import com.dekitateserver.events.util.Log
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

class TakeVoteTicketUseCase(
        private val voteTicketHistoryRepository: VoteTicketHistoryRepository
) {
    companion object {
        private const val MESSAGE_SHORT_AMOUNT = "${VoteTicket.DISPLAY_NAME}§eが{amount}枚§c不足§eしています"
    }

    suspend operator fun invoke(player: Player, amount: Int): Boolean {
        if (amount < 1) {
            Log.error("1枚以上を指定して下さい [amount: $amount]")
            return false
        }

        val playerInventory = player.inventory

        val playerTicketAmount = countIn(player, playerInventory)
        if (playerTicketAmount < amount) {
            val message = createShortMessage(amount - playerTicketAmount)
            player.sendMessage(message)
            return false
        }

        val contents = playerInventory.contents.clone()

        val removeVoteTicketResultMap = playerInventory.removeItem(VoteTicket(player, amount))
        if (removeVoteTicketResultMap.isEmpty()) {
            voteTicketHistoryRepository.add(player, -amount)
            return true
        }

        // restore contents
        playerInventory.contents = contents
        return false
    }

    private fun countIn(player: Player, inventory: PlayerInventory): Int {
        val voteTicket = VoteTicket(player)

        return inventory.contents.sumBy {
            if (it != null && voteTicket.isSimilar(it)) {
                return@sumBy it.amount
            } else {
                return@sumBy 0
            }
        }
    }

    private fun createShortMessage(amount: Int) = MESSAGE_SHORT_AMOUNT.replace("{amount}", amount.toString())
}
