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
            "join" -> sender.requireArguments(args, 3) {
                dungeonController.join(
                        sender = sender,
                        argSelector = args[1],
                        argDungeonId = args[2]
                )
            }
            "complete" -> sender.requireArguments(args, 3) {
                dungeonController.complete(
                        sender = sender,
                        argSelector = args[1],
                        argDungeonId = args[2]
                )
            }
            "exit" -> sender.requireArguments(args, 3) {
                dungeonController.exit(
                        sender = sender,
                        argSelector = args[1],
                        argDungeonId = args[2]
                )
            }
            "lock" -> sender.requireArguments(args, 3) {
                dungeonController.lock(
                        argDungeonId = args[1],
                        argSeconds = args[2]
                )
            }
            "unlock" -> sender.requireArguments(args, 2) {
                dungeonController.unlock(
                        argDungeonId = args[1]
                )
            }
            "create" -> sender.requireArguments(args, 3) {
                dungeonController.create(
                        sender = sender,
                        argDungeonId = args[1],
                        argName = args[2]
                )
            }
            "delete" -> sender.requireArguments(args, 2) {
                dungeonController.delete(
                        sender = sender,
                        argDungeonId = args[1]
                )
            }
            "edit" -> sender.requireArguments(args, 3) {
                dungeonController.edit(
                        sender = sender,
                        argDungeonId = args[1],
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
                "----- DekitateEvents Dungeonコマンドヘルプ -----",
                "| §c/dungeon join [player] [dungeonId]",
                "| §c/dungeon complete [player] [dungeonId]",
                "| §c/dungeon exit [player] [dungeonId]",
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
