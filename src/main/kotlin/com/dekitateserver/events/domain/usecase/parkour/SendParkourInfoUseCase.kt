package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.domain.repository.ParkourRepository
import com.dekitateserver.events.domain.vo.ParkourId
import com.dekitateserver.events.util.sendParkourIdNotFound
import org.bukkit.command.CommandSender

class SendParkourInfoUseCase(
        private val parkourRepository: ParkourRepository
) {
    operator fun invoke(sender: CommandSender, parkourId: ParkourId) {
        val parkour = parkourRepository.get(parkourId) ?: let {
            sender.sendParkourIdNotFound(parkourId)
            return
        }

        val messages = arrayOf(
                "§7--------- §aParkour §7---------",
                " ID: ${parkourId.value}",
                " Name: ${parkour.name}",
                " RewardEventTicketAmount: ${parkour.rewardEventTicketAmount}",
                " JoinLocation: ${parkour.joinLocation}",
                " JoinMessage: ${parkour.joinMessage}",
                " JoinBroadcastMessage: ${parkour.joinBroadcastMessage}",
                " StartMessage: ${parkour.startMessage}",
                " EndMessage: ${parkour.endMessage}",
                " EndBroadcastMessage: ${parkour.endBroadcastMessage}",
                " ExitLocation: ${parkour.exitLocation}",
                " ExitMessage: ${parkour.exitMessage}"
        )

        sender.sendMessage(messages)
    }
}
