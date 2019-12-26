package com.dekitateserver.events.data.entity

import com.dekitateserver.core.dsl.itemMeta
import com.dekitateserver.events.data.vo.KeyCompareResult
import com.dekitateserver.events.data.vo.KeyId
import com.dekitateserver.events.data.vo.MaterialLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Key(
        val id: KeyId,
        val itemStackInternal: ItemStack,
        val isEnabledTake: Boolean = true,
        val expireMinutes: Long = 0,
        val blockMaterial: Material? = null,
        val blockLocation: Location? = null,
        val matchMessage: String? = "§b一致",
        val notMatchMessage: String? = "§c不一致",
        val expiredMessage: String? = "§e期限切れ",
        val shortAmountMessage: String? = "§6数が足りません"
) {
    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        private const val DATE_FORMAT_LENGTH = 26 // 'expired @ yyyy-MM-dd HH:mm' is 26 chars
        private val ITEM_FACTORY = Bukkit.getItemFactory()
    }

    val name = itemStackInternal.itemMeta?.displayName ?: itemStackInternal.type.name
    val amount = itemStackInternal.amount

    val blockMaterialLocation = if (blockMaterial != null && blockLocation != null) {
        MaterialLocation(blockMaterial, blockLocation)
    } else null

    private val isEnabledExpire = expireMinutes > 0

    fun createItemStack(amount: Int) = itemStackInternal.clone().apply {
        setAmount(amount)

        if (isEnabledExpire) {
            itemMeta {
                val expireDateTime = LocalDateTime.now().plusMinutes(expireMinutes)

                val lore = lore ?: mutableListOf()
                lore.add("expired @ ${DATE_FORMATTER.format(expireDateTime)}")
                setLore(lore)
            }
        }
    }

    fun compareTo(target: ItemStack?): KeyCompareResult {
        if (target == null) {
            return KeyCompareResult.NOT_MATCH
        }

        if (isEnabledExpire) {
            if (!target.hasItemMeta()) {
                return KeyCompareResult.NOT_MATCH
            }

            var expireDateTimeLine = ""

            val compareTarget = target.clone()
            compareTarget.itemMeta {
                val lore = lore
                if (lore.isNullOrEmpty()) {
                    return KeyCompareResult.NOT_MATCH
                }

                expireDateTimeLine = lore.last()

                setLore(lore.dropLast(1))
            }


            if (!isSimilar(compareTarget) || expireDateTimeLine.length < DATE_FORMAT_LENGTH) {
                return KeyCompareResult.NOT_MATCH
            }

            if (isExpired(expireDateTimeLine)) {
                return KeyCompareResult.EXPIRED
            }
        } else if (!isSimilar(target)) {
            return KeyCompareResult.NOT_MATCH
        }

        return if (target.amount >= itemStackInternal.amount) {
            KeyCompareResult.MATCH
        } else {
            KeyCompareResult.SHORT_AMOUNT
        }
    }

    private fun isSimilar(target: ItemStack?): Boolean {
        if (target == null) {
            return false
        }

        return if (target == itemStackInternal) {
            true
        } else {
            itemStackInternal.type == target.type
                    && itemStackInternal.hasItemMeta() == target.hasItemMeta()
                    && (!itemStackInternal.hasItemMeta() || ITEM_FACTORY.equals(itemStackInternal.itemMeta, target.itemMeta))
        }
    }

    private fun isExpired(expireDateTimeLine: String): Boolean {
        try {
            val dateTime = LocalDateTime.parse(expireDateTimeLine, DATE_FORMATTER)
            return dateTime < LocalDateTime.now()
        } catch (e: Exception) {
        }

        return false
    }
}
