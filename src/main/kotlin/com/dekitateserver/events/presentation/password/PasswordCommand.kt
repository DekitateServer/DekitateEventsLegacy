package com.dekitateserver.events.presentation.password

import com.dekitateserver.core.bukkit.presentation.BaseCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class PasswordCommand(
    private val passwordController: PasswordController
) : BaseCommand("password") {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isHelpCommand()) {
            sender.sendMessage(MESSAGES_HELP)
            return true
        }

        when (args.first()) {
            "input" -> sender.requireArguments(args, 4) {
                passwordController.input(
                        sender = sender,
                        argSelector = args[1],
                        argPasswordId = args[2],
                        argText = args[3]
                )
            }
            "set" -> sender.requireArguments(args, 3) {
                passwordController.set(
                        argsPasswordId = args[1],
                        argText = args[2]
                )
            }
            "reset" -> sender.requireArguments(args, 3) {
                passwordController.reset(
                        sender = sender,
                        argSelector = args[1],
                        argPasswordId = args[2]
                )
            }
            "create" -> sender.requireArguments(args, 2) {
                passwordController.create(
                        sender = sender,
                        argPasswordId = args[1]
                )
            }
            "delete" -> sender.requireArguments(args, 2) {
                passwordController.delete(
                        sender = sender,
                        argPasswordId = args[1]
                )
            }
            "edit" -> sender.requireArguments(args, 3) {
                passwordController.edit(
                        sender = sender,
                        argPasswordId = args[1],
                        argType = args[2],
                        argArgs = args.drop(3)
                )
            }
            "edittype" -> passwordController.sendEditTypeList(sender)
            "list" -> passwordController.sendList(sender)
            "info" -> sender.requireArguments(args, 2) {
                passwordController.sendInfo(
                        sender = sender,
                        argPasswordId = args[1]
                )
            }
            "reload" -> passwordController.reload()
            else -> return false
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = emptyList<String>()

    companion object {
        private val MESSAGES_HELP = arrayOf(
                "----- DekitateEvents Passwordコマンドヘルプ -----",
                "| §9/password input [player] [passwordId] [text]",
                "| §9/password set [passwordId] [text]",
                "| §9/password reset [player] [passwordId]",
                "| §9/password create [passwordId]",
                "| §9/password delete [passwordId]",
                "| §9/password edit [keyId] [type] <args..>",
                "| §9/password edittype",
                "| §9/password list",
                "| §9/password info [passwordId]",
                "| §9/password reload",
                "| §9/password help",
                "| §7[]は必須,<>は任意,()は説明",
                "---------------------------------------"
        )
    }
}
