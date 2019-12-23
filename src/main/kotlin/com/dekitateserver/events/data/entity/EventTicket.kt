package com.dekitateserver.events.data.entity

import com.dekitateserver.core.dsl.itemMeta
import com.dekitateserver.core.dsl.lore
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class EventTicket(
        amount: Int = 1
) : ItemStack(Material.PAPER, amount) {

    companion object {
        const val DISPLAY_NAME = "§6§lイベントチケット§a(猫)§r"
        private const val LORE = "§3§oイベントワールドで使用する特別なチケット§r"
    }

    init {
        itemMeta {
            setDisplayName(DISPLAY_NAME)
            lore(LORE)
            addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }

    override fun isSimilar(stack: ItemStack?): Boolean {
        if (stack == null || stack.type != Material.PAPER) {
            return false
        }

        return stack.itemMeta?.lore?.firstOrNull()?.startsWith(LORE) == true
    }
}
