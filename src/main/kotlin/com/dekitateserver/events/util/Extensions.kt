package com.dekitateserver.events.util

import com.dekitateserver.events.DekitateEvents
import com.dekitateserver.events.data.vo.GachaId
import com.dekitateserver.events.data.vo.KeyId
import com.dekitateserver.events.data.vo.LoginBonusId
import com.dekitateserver.events.data.vo.PasswordId
import com.dekitateserver.events.domain.vo.ParkourId
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun CommandSender.sendSuccessMessage(message: String) = sendMessage("${DekitateEvents.PREFIX}§a$message")
fun CommandSender.sendWarnMessage(message: String) = sendMessage("${DekitateEvents.PREFIX}§e$message")

fun CommandSender.sendParkourIdNotFound(parkourId: ParkourId) = sendWarnMessage("Parkour(${parkourId.value})は存在しません")
fun CommandSender.sendGachaIdNotFound(gachaId: GachaId) = sendWarnMessage("Gacha(${gachaId.value})は存在しません")
fun CommandSender.sendKeyIdNotFound(keyId: KeyId) = sendWarnMessage("Key(${keyId.value})は存在しません")
fun CommandSender.sendPasswordIdNotFound(passwordId: PasswordId) = sendWarnMessage("Password(${passwordId.value})は存在しません")
fun CommandSender.sendLoginBonusIdNotFound(loginBonusId: LoginBonusId) = sendWarnMessage("LoginBonus(${loginBonusId.value})は存在しません")

fun CommandSender.sendCommandBlockOnly() = sendWarnMessage("コマンドブロック専用コマンドです")

fun CommandSender.toPlayerOrError(): Player? {
    if (this !is Player) {
        sendWarnMessage("プレイヤー専用コマンドです")
        return null
    }

    return this
}

fun Player.addItemOrDrop(vararg items: ItemStack) {
    val notAddedMap = inventory.addItem(*items)

    notAddedMap.forEach { (_, itemStack) ->
        world.dropItemNaturally(location, itemStack)
    }

    if (notAddedMap.isNotEmpty()) {
        sendMessage("インベントリに飽きがないためアイテムを§cドロップ§rしました")
    }
}

fun String.toPlayerOrError(): Player? = Bukkit.getPlayerExact(this) ?: let {
    Log.error("Player($it)が見つかりません")
    return null
}

fun String.toIntOrError(): Int? = toIntOrNull() ?: let {
    Log.error("'${this}'をInt(数値)に変換できません")

    return null
}

fun String.toDoubleOrError(): Double? = toDoubleOrNull() ?: let {
    Log.error("'${this}'をDouble(数値)に変換できません")

    return null
}

fun Server.selectPlayersOrError(sender: CommandSender, selector: String) = try {
    selectEntities(sender, selector).filterIsInstance<Player>()
} catch (e: IllegalArgumentException) {
    Log.error("Selector($selector)がエラーです", e)
    null
}
