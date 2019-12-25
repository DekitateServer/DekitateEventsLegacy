package com.dekitateserver.events.domain.usecase.parkour

import com.dekitateserver.events.data.ParkourRepository
import com.dekitateserver.events.data.vo.ParkourEditType
import com.dekitateserver.events.data.vo.ParkourId
import com.dekitateserver.events.util.*
import org.bukkit.command.CommandSender

class EditParkourUseCase(
        private val parkourRepository: ParkourRepository
) {
    suspend operator fun invoke(sender: CommandSender, parkourId: ParkourId, type: ParkourEditType, args: List<String>) {
        val parkour = parkourRepository.get(parkourId) ?: let {
            sender.sendParkourIdNotFound(parkourId)
            return
        }

        val newParkour = try {
            when (type) {
                ParkourEditType.NAME -> parkour.copy(name = EditArgumentsHelper.requireString(args))
                ParkourEditType.REWARD_EVENT_TICKET -> parkour.copy(rewardEventTicketAmount = EditArgumentsHelper.getInt(args)
                        ?: 0)
                ParkourEditType.JOIN_LOCATION -> {
                    val player = sender.toPlayerOrError() ?: return
                    parkour.copy(joinLocation = EditArgumentsHelper.getLocation(player, args))
                }
                ParkourEditType.JOIN_MESSAGE -> parkour.copy(joinMessage = EditArgumentsHelper.getString(args))
                ParkourEditType.JOIN_BROADCAST_MESSAGE -> parkour.copy(joinBroadcastMessage = EditArgumentsHelper.getString(args))
                ParkourEditType.START_MESSAGE -> parkour.copy(startMessage = EditArgumentsHelper.getString(args))
                ParkourEditType.END_MESSAGE -> parkour.copy(endMessage = EditArgumentsHelper.getString(args))
                ParkourEditType.END_BROADCAST_MESSAGE -> parkour.copy(endBroadcastMessage = EditArgumentsHelper.getString(args))
                ParkourEditType.EXIT_LOCATION -> {
                    val player = sender.toPlayerOrError() ?: return
                    parkour.copy(exitLocation = EditArgumentsHelper.getLocation(player, args))
                }
                ParkourEditType.EXIT_MESSAGE -> parkour.copy(exitMessage = EditArgumentsHelper.getString(args))
            }
        } catch (e: IllegalArgumentException) {
            sender.sendWarnMessage(e.message ?: "不明なエラーです.")
            return
        }

        if (parkourRepository.update(newParkour)) {
            sender.sendSuccessMessage("Parkour(${parkourId.value})を更新しました.")
        } else {
            sender.sendWarnMessage("Parkour(${parkourId.value})の更新に失敗しました.")
        }
    }
}
