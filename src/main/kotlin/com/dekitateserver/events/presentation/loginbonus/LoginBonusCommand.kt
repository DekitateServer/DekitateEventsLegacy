package com.dekitateserver.events.presentation.loginbonus

import com.dekitateserver.core.command.AbstractCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class LoginBonusCommand(
        private val loginBonusController: LoginBonusController
) : AbstractCommand("loginbonus") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isHelpCommand()) {
            sender.sendMessage(MESSAGES_HELP)
            return true
        }

        when (args.first()) {
            "info" -> sender.requireArguments(args, 2) {
                loginBonusController.sendInfo(
                        sender = sender,
                        argLoginBonusId = args[1]
                )
            }
            "list" -> loginBonusController.sendList(sender)
            "reload" -> loginBonusController.reload()
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = emptyList<String>()

    companion object {
        private val MESSAGES_HELP = arrayOf(
                "----- DekitateEvents LoginBonusコマンドヘルプ -----",
                "| §6/loginbonus info [loginBonusId]",
                "| §6/loginbonus list",
                "| §6/loginbonus reload",
                "| §6/loginbonus help",
                "| §7[]は必須,<>は任意,()は説明です.",
                "---------------------------------------"
        )
    }
}
