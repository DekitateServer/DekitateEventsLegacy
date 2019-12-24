package com.dekitateserver.events.presentation.parkour

import com.dekitateserver.core.command.AbstractCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ParkourCommand : AbstractCommand("parkour") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isHelpCommand()) {
            sender.sendMessage(MESSAGES_HELP)
            return true
        }

        when (args.first()) {
            else -> return false
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = emptyList<String>()

    companion object {
        private val MESSAGES_HELP = arrayOf(
                "----- DekitateEvents Parkourコマンドヘルプ -----",
                "| §a/parkour join [player] [parkourId]",
                "| §a/parkour clear [player] [parkourId]",
                "| §a/parkour exit [player] [parkourId]",
                "| §a/parkour create [parkourId]",
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
