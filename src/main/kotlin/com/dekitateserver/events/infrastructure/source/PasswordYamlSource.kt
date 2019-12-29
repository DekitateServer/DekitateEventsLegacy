package com.dekitateserver.events.infrastructure.source

import com.dekitateserver.core.data.source.YamlStorage
import com.dekitateserver.events.domain.entity.Password
import com.dekitateserver.events.domain.vo.PasswordId
import com.dekitateserver.events.util.Log
import org.bukkit.Location
import org.bukkit.Material
import java.io.File

class PasswordYamlSource(dataFolder: File) {

    private val storage = YamlStorage(File(dataFolder, "password.yml"))

    fun getAll(): List<Password> {
        val passwordList = mutableListOf<Password>()

        try {
            val config = storage.loadYamlConfiguration()

            val passwordIdSet = config.root?.getKeys(false).orEmpty()

            val list = passwordIdSet.mapNotNull { id ->
                return@mapNotNull Password(
                        id = PasswordId(id),
                        value = config.getString("$id.value").orEmpty(),
                        blockMaterial = config.getString("$id.Block.material")?.let { Material.matchMaterial(it) },
                        blockLocation = config.getSerializable("$id.Block.Location", Location::class.java),
                        matchMessage = config.getString("$id.matchMessage"),
                        notMatchMessage = config.getString("$id.notMatchMessage"),
                        inputMessage = config.getString("$id.inputMessage"),
                        resetMessage = config.getString("$id.resetMessage")
                )
            }

            passwordList.addAll(list)
        } catch (e: Exception) {
            Log.error("Passwordの読み込みに失敗しました", e)
        }

        return passwordList
    }

    fun set(password: Password): Boolean {
        val dataMap = linkedMapOf<String, Any?>().apply {
            val path = password.id.value

            put("$path.value", password.get())

            put("$path.Block.material", password.blockMaterial?.name)
            put("$path.Block.Location", password.blockLocation)

            put("$path.matchMessage", password.matchMessage)
            put("$path.notMatchMessage", password.notMatchMessage)
            put("$path.inputMessage", password.inputMessage)
            put("$path.resetMessage", password.resetMessage)
        }

        return storage.save(dataMap)
    }

    fun delete(passwordId: PasswordId) = storage.save(mapOf(passwordId.value to null))
}
