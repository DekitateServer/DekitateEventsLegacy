package com.dekitateserver.events.presentation.event

import com.dekitateserver.core.bukkit.presentation.BaseCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class EventCommand(
    private val eventController: EventController
) : BaseCommand("event") {

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
            "tp" -> sender.requireArguments(args, 5) {
                eventController.teleport(
                    sender = sender,
                    argSelector = args[1],
                    argX = args[2],
                    argY = args[3],
                    argZ = args[4],
                    argYaw = args.getOrNull(5),
                    argPitch = args.getOrNull(6)
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
            "| §e/event tp [player] [x y z <yaw pitch>] (相対指定時の基準はプレイヤー座標)",
            "| §e/event msg [player] [sender] [message] ([sender]にnullを指定で[message]のみ表示)",
            "| §e/event setspawn [player] [x y z] (相対指定時の基準はｺﾏﾝﾄﾞﾌﾞﾛｯｸ座標)",
            "| §e/event jump [player] [height] [length] (各値は小数点係数)",
            "| §e/event exp [player] [value] (経験値ｵｰﾌﾞをドロップ)",
            "| §e/event random [x y z] [x y z] <x y z> (座標は複数指定可能. 0.5s後に消滅)",
            "| §e/event randomtp [x y z] [x y z] <x y z> (座標は複数指定可能)",
            "| §e/event delay [seconds] [x y z]",
            "| §e/event canceldelay [x y z]",
            "| §e/event notdelay [x y z] [x y z] (delay位置, RED_STONE位置)",
            "| §e/event reset_status [player] (プレイヤーのステータスをリセットして体力20)",
            "| §e/event reload",
            "| §e/event help",
            "| §d/gacha",
            "| §6/key",
            "| §9/password",
            "| §a/parkour",
            "| §c/dungeon",
            "| §7[]は必須,<>は任意,()は説明",
            "---------------------------------------"
        )
    }
}
