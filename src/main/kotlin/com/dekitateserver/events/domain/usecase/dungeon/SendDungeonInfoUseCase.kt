package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.sendDungeonIdNotFound
import org.bukkit.command.CommandSender

class SendDungeonInfoUseCase(
        private val dungeonRepository: DungeonRepository
) {
    operator fun invoke(sender: CommandSender, dungeonId: DungeonId) {
        val dungeon = dungeonRepository.get(dungeonId) ?: let {
            sender.sendDungeonIdNotFound(dungeonId)
            return
        }

        val messages = arrayOf(
                "§7--------- §cDungeon §7---------",
                " ID: ${dungeonId.value}",
                " Name: ${dungeon.name}",
                " RewardEventTicketAmount: ${dungeon.rewardEventTicketAmount}",
                " RewardGachaId: ${dungeon.rewardGachaId?.value}",
                " JoinLocation: ${dungeon.joinLocation}",
                " JoinMessage: ${dungeon.joinMessage}",
                " JoinBroadcastMessage: ${dungeon.joinBroadcastMessage}",
                " CompleteLocation: ${dungeon.completeLocation}",
                " CompleteMessage: ${dungeon.completeMessage}",
                " CompleteBroadcastMessage: ${dungeon.completeBroadcastMessage}",
                " EnabledCompleteSound: ${dungeon.isEnabledCompleteSound}",
                " ExitLocation: ${dungeon.exitLocation}",
                " ExitMessage: ${dungeon.exitMessage}",
                " LockEndDateTime: ${dungeon.lockEndDateTime}",
                " LockMessage: ${dungeon.lockMessage}",
                " UnlockBroadcastMessage: ${dungeon.unlockBroadcastMessage}"
        )

        sender.sendMessage(messages)
    }
}
