package com.dekitateserver.events.util

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EditArgumentsHelper {

    fun getBoolean(args: List<String>): Boolean? {
        val text = args.firstOrNull() ?: return null
        return when (text) {
            "on" -> true
            "off" -> false
            else -> throw IllegalArgumentException("(on|off)を指定してください.")
        }
    }

    fun getLong(args: List<String>): Long? {
        val text = args.firstOrNull() ?: return null
        return text.toLongOrNull() ?: throw IllegalArgumentException("整数を指定してください.")
    }

    fun getString(args: List<String>): String? {
        if (args.isEmpty()) {
            return null
        }

        return args.joinToString(separator = " ")
    }

    fun getItemInMainHand(player: Player): ItemStack {
        val itemInMainHand = player.inventory.itemInMainHand
        if (itemInMainHand.type == Material.AIR) {
            throw IllegalArgumentException("アイテムをメインハンドに持ってください.")
        }

        return itemInMainHand
    }

    fun getLocation(player: Player, args: List<String>): Location? {
        if (args.isEmpty()) {
            return null
        }

        if (args.first() == "here") {
            return player.location
        } else if (args.size < 3) {
            throw IllegalArgumentException("正しい位置情報を指定してください.(here|X Y Z)")
        }

        return player.location.apply {
            x = args[0].toDoubleOrNull() ?: throw IllegalArgumentException("数値に変換できません.")
            y = args[1].toDoubleOrNull() ?: throw IllegalArgumentException("数値に変換できません.")
            z = args[2].toDoubleOrNull() ?: throw IllegalArgumentException("数値に変換できません.")

            if (args.size >= 5) {
                yaw = args[3].toFloatOrNull() ?: throw IllegalArgumentException("数値に変換できません.")
                pitch = args[4].toFloatOrNull() ?: throw IllegalArgumentException("数値に変換できません.")
            }
        }
    }

    fun getMaterial(args: List<String>): Material? {
        if (args.isEmpty()) {
            return null
        }

        return Material.matchMaterial(args.first()) ?: throw IllegalArgumentException("正しい素材を指定してください.")
    }
}
