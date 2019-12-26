package com.dekitateserver.events.presentation.key

import com.dekitateserver.core.command.AbstractCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class KeyCommand(
        private val keyController: KeyController
) : AbstractCommand("key") {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isHelpCommand()) {
            sender.sendMessage(MESSAGES_HELP)
            return true
        }

        when (args.first()) {
            "use" -> sender.requireArguments(args, 3) {
                keyController.use(
                        sender = sender,
                        argSelector = args[1],
                        argKeyId = args[2]
                )
            }
            "give" -> sender.requireArguments(args, 3) {
                keyController.give(
                        sender = sender,
                        argSelector = args[1],
                        argKeyId = args[2],
                        argAmount = args.getOrNull(3)
                )
            }
            "create" -> sender.requireArguments(args, 2) {
                keyController.create(
                        sender = sender,
                        argKeyId = args[1]
                )
            }
            "delete" -> sender.requireArguments(args, 2) {
                keyController.delete(
                        sender = sender,
                        argKeyId = args[1]
                )
            }
            "edit" -> sender.requireArguments(args, 3) {
                keyController.edit(
                        sender = sender,
                        argKeyId = args[1],
                        argType = args[2],
                        argArgs = args.drop(3)
                )
            }
            "edittype" -> keyController.sendEditTypeList(sender)
            "list" -> keyController.sendList(sender)
            "info" -> sender.requireArguments(args, 2) {
                keyController.sendInfo(
                        sender = sender,
                        argKeyId = args[1]
                )
            }
            "reload" -> keyController.reload()
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = emptyList<String>()

    companion object {
        private val MESSAGES_HELP = arrayOf(
                "----- DekitateEvents Keyコマンドヘルプ -----",
                "| §6/key use [player] [keyId]",
                "| §6/key give [player] [keyId] <amount>",
                "| §6/key create [keyId]",
                "| §6/key delete [keyId]",
                "| §6/key edit [keyId] [type] <args..>",
                "| §6/key edittype",
                "| §6/key list",
                "| §6/key info [keyId]",
                "| §6/key reload",
                "| §6/key help",
                "| §7[]は必須,<>は任意,()は説明",
                "---------------------------------------"
        )
    }
}
