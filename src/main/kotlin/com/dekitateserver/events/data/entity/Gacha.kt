package com.dekitateserver.events.data.entity

import com.dekitateserver.core.util.MersenneTwisterFast
import com.dekitateserver.events.data.vo.GachaId

data class Gacha(
        val id: GachaId,
        val name: String,
        private val itemList: List<Item>,
        val winMessage: String?,
        val loseMessage: String?,
        val winBroadcastMessage: String?,
        val isEnabledEffect: Boolean
) {

    companion object {
        private val RANDOM = MersenneTwisterFast()
    }

    data class Item(
            val id: String,
            val name: String,
            val probability: Double,
            val commandList: List<String>,
            val isDisabledBroadcast: Boolean,
            val isEnabledOncePerDay: Boolean
    ) {
        val isWin = commandList.isNotEmpty()
    }

    val itemSize = itemList.size

    init {
        require(itemList.sumByDouble { it.probability } == 1.0) {
            "Gacha(${id.value})の合計確率が100%ではありません"
        }
    }

    fun getItemByRandom(): Item {
        val random = RANDOM.nextDouble(false, true)
        var count = 0.0

        for (item in itemList) {
            count += item.probability

            if (count >= random) {
                return item
            }
        }

        throw IllegalStateException("Gacha(${id.value})'s probability is broken")
    }
}
