package com.dekitateserver.events.domain.entity

import com.dekitateserver.core.dsl.itemMeta
import com.dekitateserver.core.dsl.lore
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class VoteTicket(
        player: Player,
        amount: Int = 1
) : ItemStack(Material.PAPER, amount) {

    companion object {
        const val DISPLAY_NAME = "§a§l投票限定チケット§6(猫)§r"
        private const val LORE_LINE0 = "§6§o投票§3§oでもらえる不思議なチケット§r #"
        private const val LORE_LINE1 = "§8本人しか使用できないよ(｡･ω･｡)"
    }

    private val loreLine0 = LORE_LINE0 + player.name

    init {
        itemMeta {
            setDisplayName(DISPLAY_NAME)
            lore(loreLine0, LORE_LINE1)
            addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }

    override fun isSimilar(stack: ItemStack?): Boolean {
        if (stack == null || stack.type != Material.PAPER) {
            return false
        }

        val lore = stack.itemMeta?.lore ?: return false
        if (lore.size < 2) {
            return false
        }

        return lore[0].startsWith(loreLine0) && lore[1].startsWith(LORE_LINE1)
    }
}
