package com.dekitateserver.events.presentation.parkour

import com.dekitateserver.core.command.AbstractCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ParkourCommand(
        private val parkourController: ParkourController
) : AbstractCommand("parkour") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isHelpCommand()) {
            sender.sendMessage(MESSAGES_HELP)
            return true
        }

        when (args.first()) {
            "join" -> sender.requireArguments(args, 3) {
                parkourController.join(
                        sender = sender,
                        argSelector = args[1],
                        argParkourId = args[2]
                )
            }
            "start" -> sender.requireArguments(args, 3) {
                parkourController.start(
                        sender = sender,
                        argSelector = args[1],
                        argParkourId = args[2]
                )
            }
            "end" -> sender.requireArguments(args, 3) {
                parkourController.end(
                        sender = sender,
                        argSelector = args[1],
                        argParkourId = args[2]
                )
            }
            "exit" -> sender.requireArguments(args, 3) {
                parkourController.exit(
                        sender = sender,
                        argSelector = args[1],
                        argParkourId = args[2]
                )
            }
            "create" -> sender.requireArguments(args, 3) {
                parkourController.create(
                        sender = sender,
                        argParkourId = args[1],
                        argName = args[2]
                )
            }
            "delete" -> sender.requireArguments(args, 2) {
                parkourController.delete(
                        sender = sender,
                        argParkourId = args[1]
                )
            }
            "edit" -> sender.requireArguments(args, 3) {
                parkourController.edit(
                        sender = sender,
                        argParkourId = args[1],
                        argType = args[2],
                        argArgs = args.drop(3)
                )
            }
            else -> return false
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = emptyList<String>()

    companion object {
        private val MESSAGES_HELP = arrayOf(
                "----- DekitateEvents Parkourコマンドヘルプ -----",
                "| §a/parkour join [player] [parkourId]",
                "| §a/parkour start [player] [parkourId]",
                "| §a/parkour end [player] [parkourId]",
                "| §a/parkour exit [player] [parkourId]",
                "| §a/parkour create [parkourId] [name]",
                "| §a/parkour delete [parkourId]",
                "| §a/parkour edit [parkourId] [type] <args..>",
                "| §a/parkour edittype",
                "| §a/parkour list",
                "| §a/parkour info [parkourId]",
                "| §a/parkour reload",
                "| §a/parkour help",
                "| §7[]は必須,<>は任意,()は説明です.",
                "---------------------------------------"
        )
    }
}
