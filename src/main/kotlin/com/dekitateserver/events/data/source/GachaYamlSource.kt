package com.dekitateserver.events.data.source

import com.dekitateserver.core.data.source.YamlStorage
import com.dekitateserver.events.data.entity.Gacha
import com.dekitateserver.events.data.vo.GachaId
import com.dekitateserver.events.util.Log
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class GachaYamlSource(dataFolder: File) {

    private val storage = YamlStorage(File(dataFolder, "gacha.yml"))

    fun getAll(): List<Gacha> {
        val gachaList = mutableListOf<Gacha>()

        try {
            val config = YamlConfiguration.loadConfiguration(storage.requireFile())

            val gachaIdSet = config.root?.getKeys(false).orEmpty()

            val list = gachaIdSet.mapNotNull { gachaId ->
                val itemPath = "$gachaId.Items"
                val itemKeySet = config.getConfigurationSection(itemPath)?.getKeys(false).orEmpty()
                val itemList = itemKeySet.map { itemId ->
                    val path = "$itemPath.$itemId"

                    return@map Gacha.Item(
                            id = itemId,
                            name = config.getString("$path.name").orEmpty(),
                            probability = config.getDouble("$path.probability"),
                            commandList = config.getStringList("$path.commands"),
                            isDisabledBroadcast = config.getBoolean("$path.disabledBroadcast"),
                            isEnabledOncePerDay = config.getBoolean("$path.enabledOncePerDay")
                    )
                }.normalizeProbability()

                if (itemList.isEmpty()) {
                    Log.warn("Gacha($gachaId)の'Items'が空です.")
                    return@mapNotNull null
                }


                return@mapNotNull Gacha(
                        id = GachaId(gachaId),
                        name = config.getString("$gachaId.name") ?: "gacha",
                        itemList = itemList,
                        winMessage = config.getString("$gachaId.winMessage"),
                        loseMessage = config.getString("$gachaId.loseMessage"),
                        winBroadcastMessage = config.getString("$gachaId.winBroadcastMessage"),
                        isEnabledEffect = config.getBoolean("$gachaId.enabledEffect")
                )
            }

            gachaList.addAll(list)
        } catch (e: Exception) {
            Log.error("Gachaの読み込みに失敗しました.", e)
        }

        return gachaList
    }

    private fun List<Gacha.Item>.normalizeProbability(): List<Gacha.Item> {
        val sumProbability = sumByDouble { it.probability }
        if (sumProbability > 1.0) {
            Log.warn("Gachaの合計確率が100%を超えています.[sumProbability: $sumProbability]")
            return emptyList()
        }

        val undefinedProbabilityCount = count { it.probability <= 0.0 }

        return if (undefinedProbabilityCount > 0) {
            val undefinedProbability = (1.0 - sumProbability) / undefinedProbabilityCount

            map {
                if (it.probability <= 0.0) {
                    it.copy(probability = undefinedProbability)
                } else {
                    it
                }
            }
        } else {
            val gain = 1.0 / sumProbability

            map {
                it.copy(probability = it.probability * gain)
            }
        }
    }
}
