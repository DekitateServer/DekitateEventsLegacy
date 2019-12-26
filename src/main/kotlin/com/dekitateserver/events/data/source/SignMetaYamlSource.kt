package com.dekitateserver.events.data.source

import com.dekitateserver.core.data.source.YamlStorage
import com.dekitateserver.events.data.entity.SignMeta
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class SignMetaYamlSource(dataFolder: File) {

    private val storage = YamlStorage(File(dataFolder, "sign_meta.yml"))

    fun getAll(): List<SignMeta> {
        val signMetaList = mutableListOf<SignMeta>()

        try {
            val config = storage.loadYamlConfiguration()

            val signMetaKeySet = config.root?.getKeys(false).orEmpty().map { it.toInt() }

            val list = signMetaKeySet.map { key ->
                val location = config.getSerializable("$key.Location", Location::class.java)

                val metaPath = "$key.Meta"

                val metaMap = config.getConfigurationSection(metaPath)?.getKeys(false)
                        .orEmpty()
                        .associateWith {
                            requireNotNull(config.get("$metaPath.$it"))
                        }

                return@map SignMeta(
                        location = requireNotNull(location),
                        key = key,
                        dataMap = metaMap
                )
            }

            signMetaList.addAll(list)
        } catch (e: Exception) {
            Log.error("SignMetaの読み込みに失敗しました", e)
        }

        return signMetaList
    }

    fun save(signMeta: SignMeta): Boolean {
        val signMetaKey = signMeta.key ?: storage.loadYamlConfiguration().findAvailableKey()

        val dataMap = linkedMapOf<String, Any>().apply {
            put("$signMetaKey.Location", signMeta.location)

            val metaPath = "$signMetaKey.Meta."
            signMeta.dataMap.forEach { (key, value) ->
                put("$metaPath$key", value)
            }
        }

        return storage.save(dataMap)
    }

    private fun YamlConfiguration.findAvailableKey(): Int {
        var previousKey = 0

        root?.getKeys(false).orEmpty()
                .map { it.toInt() }
                .sorted()
                .forEach { currentKey ->
                    if (currentKey - previousKey > 1) {
                        return previousKey + 1
                    }

                    previousKey = currentKey
                }

        return previousKey + 1
    }
}
