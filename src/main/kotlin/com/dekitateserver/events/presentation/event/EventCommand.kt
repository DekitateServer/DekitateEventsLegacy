package com.dekitateserver.events.presentation.event

import com.dekitateserver.core.command.AbstractCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class EventCommand(
        private val eventController: EventController
) : AbstractCommand("event") {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isHelpCommand()) {
            sender.sendMessage(MESSAGES_HELP)
            return true
        }

        when (args.first()) {
            "ticket" -> sender.requireArguments(args, 3) {
                eventController.giveTicket(
                        sender = sender,
                        argSelector = args[1],
                        argTicketType = args[2],
                        argAmount = args[3]
                )
            }
            "setspawn" -> sender.requireArguments(args, 5) {
                eventController.setSpawn(
                        sender = sender,
                        argSelector = args[1],
                        argX = args[2],
                        argY = args[3],
                        argZ = args[4]
                )
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = emptyList<String>()

    companion object {
        private val MESSAGES_HELP = arrayOf(
                "---------- DekitateEvents コマンドヘルプ ----------",
                "| §e/event ticket [player] [ticketType] [amount]",
                "| §e/event setspawn [player] [x y z]",
                "| §7[]は必須,<>は任意,()は説明",
                "---------------------------------------"
        )
    }
}
