package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonEditType
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.domain.vo.GachaId
import com.dekitateserver.events.util.*
import org.bukkit.command.CommandSender

class EditDungeonUseCase(
        private val dungeonRepository: DungeonRepository
) {
    suspend operator fun invoke(sender: CommandSender, dungeonId: DungeonId, type: DungeonEditType, args: List<String>) {
        val dungeon = dungeonRepository.get(dungeonId) ?: let {
            sender.sendDungeonIdNotFound(dungeonId)
            return
        }

        val newDungeon = try {
            when (type) {
                DungeonEditType.NAME -> dungeon.copy(name = EditArgumentsHelper.requireString(args))
                DungeonEditType.REWARD_TICKET -> dungeon.copy(
                        rewardEventTicketAmount = EditArgumentsHelper.getInt(args) ?: 0
                )
                DungeonEditType.REWARD_GACHA -> dungeon.copy(
                        rewardGachaId = EditArgumentsHelper.getString(args)?.let { GachaId(it) }
                )
                DungeonEditType.JOIN_LOCATION -> {
                    val player = sender.toPlayerOrError() ?: return
                    dungeon.copy(joinLocation = EditArgumentsHelper.getLocation(player, args))
                }
                DungeonEditType.JOIN_MESSAGE -> dungeon.copy(joinMessage = EditArgumentsHelper.getString(args))
                DungeonEditType.JOIN_BROADCAST_MESSAGE -> dungeon.copy(joinBroadcastMessage = EditArgumentsHelper.getString(args))
                DungeonEditType.COMPLETE_LOCATION -> {
                    val player = sender.toPlayerOrError() ?: return
                    dungeon.copy(completeLocation = EditArgumentsHelper.getLocation(player, args))
                }
                DungeonEditType.COMPLETE_MESSAGE -> dungeon.copy(completeMessage = EditArgumentsHelper.getString(args))
                DungeonEditType.COMPLETE_BROADCAST_MESSAGE -> dungeon.copy(completeBroadcastMessage = EditArgumentsHelper.getString(args))
                DungeonEditType.COMPLETE_SOUND -> dungeon.copy(
                        isEnabledCompleteSound = EditArgumentsHelper.getBoolean(args) ?: false
                )
                DungeonEditType.EXIT_LOCATION -> {
                    val player = sender.toPlayerOrError() ?: return
                    dungeon.copy(exitLocation = EditArgumentsHelper.getLocation(player, args))
                }
                DungeonEditType.EXIT_MESSAGE -> dungeon.copy(exitMessage = EditArgumentsHelper.getString(args))
                DungeonEditType.LOCK_MESSAGE -> dungeon.copy(lockMessage = EditArgumentsHelper.getString(args))
                DungeonEditType.LOCK_BROADCAST_MESSAGE -> dungeon.copy(lockBroadcastMessage = EditArgumentsHelper.getString(args))
                DungeonEditType.UNLOCK_BROADCAST_MESSAGE -> dungeon.copy(unlockBroadcastMessage = EditArgumentsHelper.getString(args))
            }
        } catch (e: IllegalArgumentException) {
            sender.sendWarnMessage(e.message ?: "不明なエラーです")
            return
        }

        if (dungeonRepository.update(newDungeon)) {
            sender.sendSuccessMessage("Dungeon(${dungeonId.value})を更新しました")
        } else {
            sender.sendWarnMessage("Dungeon(${dungeonId.value})の更新に失敗しました")
        }
    }
}
