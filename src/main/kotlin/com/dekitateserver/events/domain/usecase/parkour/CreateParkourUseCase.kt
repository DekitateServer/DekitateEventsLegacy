package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.core.bukkit.util.formatColorCodes
import com.dekitateserver.events.domain.entity.Parkour
import com.dekitateserver.events.domain.repository.ParkourRepository
import com.dekitateserver.events.domain.vo.ParkourId
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class CreateParkourUseCase(
        private val parkourRepository: ParkourRepository
) {
    suspend operator fun invoke(sender: CommandSender, parkourId: ParkourId, name: String) {
        if (parkourRepository.has(parkourId)) {
            sender.sendWarnMessage("Parkour(${parkourId.value})は既に登録済みです")
            return
        }

        val parkour = Parkour(
                id = parkourId,
                name = name.formatColorCodes()
        )

        if (parkourRepository.add(parkour)) {
            sender.sendSuccessMessage("Parkour(${parkourId.value})を作成しました")
        } else {
            sender.sendWarnMessage("Parkour(${parkourId.value})の作成に失敗しました")
        }
    }
}
