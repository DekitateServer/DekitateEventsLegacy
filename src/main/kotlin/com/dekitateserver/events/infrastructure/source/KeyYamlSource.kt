package com.dekitateserver.events.infrastructure.source

import com.dekitateserver.core.data.source.YamlStorage
import com.dekitateserver.events.data.entity.Key
import com.dekitateserver.events.domain.vo.KeyId
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.Material
import java.io.File

class KeyYamlSource(dataFolder: File) {
    private val storage = YamlStorage(File(dataFolder, "key.yml"))

    fun getAll(): List<Key> {
        val keyList = mutableListOf<Key>()

        try {
            val config = storage.loadYamlConfiguration()

            val keyIdSet = config.root?.getKeys(false).orEmpty()

            val list = keyIdSet.mapNotNull { id ->
                val keyId = KeyId(id)

                val itemStack = config.getItemStack("$id.ItemStack") ?: let {
                    Log.warn("Key($id)のItemStackがnullです")
                    return@mapNotNull null
                }

                return@mapNotNull Key(
                        id = keyId,
                        itemStackInternal = itemStack,
                        isEnabledTake = config.getBoolean("$id.enabledTake"),
                        expireMinutes = config.getLong("$id.expireMinutes"),
                        blockMaterial = config.getString("$id.Block.material")?.let { Material.matchMaterial(it) },
                        blockLocation = config.getSerializable("$id.Block.Location", Location::class.java),
                        matchMessage = config.getString("$id.matchMessage"),
                        notMatchMessage = config.getString("$id.notMatchMessage"),
                        expiredMessage = config.getString("$id.expiredMessage"),
                        shortAmountMessage = config.getString("$id.shortAmountMessage")
                )
            }

            keyList.addAll(list)
        } catch (e: Exception) {
            Log.error("Keyの読み込みに失敗しました", e)
        }

        return keyList
    }

    fun set(key: Key): Boolean {
        val dataMap = linkedMapOf<String, Any?>().apply {
            val path = key.id.value

            put("$path.enabledTake", key.isEnabledTake)
            put("$path.expireMinutes", key.expireMinutes)

            put("$path.Block.material", key.blockMaterial?.name)
            put("$path.Block.Location", key.blockLocation)

            put("$path.matchMessage", key.matchMessage)
            put("$path.notMatchMessage", key.notMatchMessage)
            put("$path.expiredMessage", key.expiredMessage)
            put("$path.shortAmountMessage", key.shortAmountMessage)

            put("$path.ItemStack", key.itemStackInternal)
        }

        return storage.save(dataMap)
    }

    fun delete(keyId: KeyId) = storage.save(mapOf(keyId.value to null))
}
