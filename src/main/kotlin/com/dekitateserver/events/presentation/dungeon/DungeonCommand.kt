package com.dekitateserver.events.presentation.dungeon

import com.dekitateserver.core.command.AbstractCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class DungeonCommand(
        private val dungeonController: DungeonController
) : AbstractCommand("dungeon") {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isHelpCommand()) {
            sender.sendMessage(MESSAGES_HELP)
            return true
        }

        when (args.first()) {
            "create" -> sender.requireArguments(args, 3) {
                dungeonController.create(
                        sender = sender,
                        argDungeonId = args[1],
                        argName = args[2]
                )
            }
            else -> return false
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = emptyList<String>()

    companion object {
        private val MESSAGES_HELP = arrayOf(
                "----- DekitateEvents Dungeonコマンドヘルプ -----",
                "| §c/dungeon join [player] [dungeonId]",
                "| §c/dungeon complete [player] [dungeonId]",
                "| §c/dungeon lock [dungeonId] [seconds]",
                "| §c/dungeon unlock [dungeonId]",
                "| §c/dungeon create [dungeonId] [name]",
                "| §c/dungeon delete [dungeonId]",
                "| §c/dungeon edit [dungeonId] [type] <args..>",
                "| §c/dungeon edittype",
                "| §c/dungeon list",
                "| §c/dungeon info [dungeonId]",
                "| §c/dungeon reload",
                "| §c/dungeon help",
                "| §7[]は必須,<>は任意,()は説明",
                "---------------------------------------"
        )
    }
}
