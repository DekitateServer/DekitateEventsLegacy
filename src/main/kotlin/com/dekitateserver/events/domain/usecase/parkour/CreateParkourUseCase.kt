package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.data.ParkourRepository
import com.dekitateserver.events.data.entity.Parkour
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class CreateParkourUseCase(
        private val parkourRepository: ParkourRepository
) {
    suspend operator fun invoke(sender: CommandSender, parkourId: ParkourId) {
        if (parkourRepository.has(parkourId)) {
            sender.sendWarnMessage("Parkour(${parkourId.value})は既に登録済みです.")
            return
        }

        val parkour = Parkour(parkourId)

        if (parkourRepository.add(parkour)) {
            sender.sendSuccessMessage("Parkour(${parkourId.value})を作成しました.")
        } else {
            sender.sendWarnMessage("Parkour(${parkourId.value})の作成に失敗しました.")
        }
    }
}
