package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.sendDungeonIdNotFound
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class DeleteDungeonUseCase(
        private val dungeonRepository: DungeonRepository
) {
    suspend operator fun invoke(sender: CommandSender, dungeonId: DungeonId) {
        if (!dungeonRepository.has(dungeonId)) {
            sender.sendDungeonIdNotFound(dungeonId)
            return
        }

        if (dungeonRepository.remove(dungeonId)) {
            sender.sendSuccessMessage("Dungeon(${dungeonId.value})を消去しました")
        } else {
            sender.sendWarnMessage("Dungeon(${dungeonId.value})の消去に失敗しました")
        }
    }
}
