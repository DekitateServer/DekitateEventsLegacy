package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.data.ParkourRepository
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.util.sendParkourIdNotFound
import com.dekitateserver.events.util.sendSuccessMessage
import com.dekitateserver.events.util.sendWarnMessage
import org.bukkit.command.CommandSender

class DeleteParkourUseCase(
        private val parkourRepository: ParkourRepository
) {
    suspend operator fun invoke(sender: CommandSender, parkourId: ParkourId) {
        if (!parkourRepository.has(parkourId)) {
            sender.sendParkourIdNotFound(parkourId)
            return
        }

        if (parkourRepository.remove(parkourId)) {
            sender.sendSuccessMessage("Parkour(${parkourId.value})を消去しました.")
        } else {
            sender.sendWarnMessage("Parkour(${parkourId.value})の消去に失敗しました.")
        }
    }
}
