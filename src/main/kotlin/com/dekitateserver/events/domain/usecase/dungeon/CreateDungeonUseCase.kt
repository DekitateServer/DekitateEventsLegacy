package com.dekitateserver.events.domain.usecase.dungeon

import com.dekitateserver.core.util.formatColorCodes
import com.dekitateserver.events.domain.entity.Dungeon
import com.dekitateserver.events.domain.repository.DungeonRepository
import com.dekitateserver.events.domain.vo.DungeonId
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class CreateDungeonUseCase(
        private val dungeonRepository: DungeonRepository
) {
    suspend operator fun invoke(sender: CommandSender, dungeonId: DungeonId, name: String) {
        if (dungeonRepository.has(dungeonId)) {
            sender.sendWarnMessage("Dungeon(${dungeonId.value})は既に登録済みです")
            return
        }

        val dungeon = Dungeon(
                id = dungeonId,
                name = name.formatColorCodes()
        )

        if (dungeonRepository.add(dungeon)) {
            sender.sendSuccessMessage("Dungeon(${dungeonId.value})を作成しました")
        } else {
            sender.sendWarnMessage("Dungeon(${dungeonId.value})の作成に失敗しました")
        }
    }
}
