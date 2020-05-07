package com.dekitateserver.events.presentation.gacha

import com.dekitateserver.core.bukkit.presentation.BaseCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class GachaCommand(
    private val gachaController: GachaController
) : BaseCommand("gacha") {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isHelpCommand()) {
            sender.sendMessage(MESSAGES_HELP)
            return true
        }

        when (args.first()) {
            "play" -> sender.requireArguments(args, 3) {
                gachaController.play(
                        sender = sender,
                        argSelector = args[1],
                        argGachaId = args[2]
                )
            }
            "demo" -> sender.requireArguments(args, 3) {
                gachaController.demo(
                        sender = sender,
                        argGachaId = args[1],
                        argTimes = args[2]
                )
            }
            "list" -> gachaController.sendList(sender)
            "info" -> sender.requireArguments(args, 2) {
                gachaController.sendInfo(
                        sender = sender,
                        argGachaId = args[1]
                )
            }
            "reload" -> gachaController.reload()
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = emptyList<String>()

    companion object {
        private val MESSAGES_HELP = arrayOf(
                "----- DekitateEvents Gachaコマンドヘルプ -----",
                "| §d/gacha play [player] [gachaId]",
                "| §d/gacha demo [gachaId] [times]",
                "| §d/gacha list",
                "| §d/gacha info [gachaId]",
                "| §d/gacha reload",
                "| §d/gacha help",
                "| §7[]は必須,<>は任意,()は説明",
                "---------------------------------------"
        )
    }
}
