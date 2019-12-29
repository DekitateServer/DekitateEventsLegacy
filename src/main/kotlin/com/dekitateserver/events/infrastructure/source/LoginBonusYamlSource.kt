package com.dekitateserver.events.infrastructure.source

import com.dekitateserver.core.data.source.YamlStorage
import com.dekitateserver.events.data.entity.LoginBonus
import com.dekitateserver.events.domain.vo.LoginBonusId
import com.dekitateserver.events.util.Log
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginBonusYamlSource(dataFolder: File) {

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    }

    private val storage = YamlStorage(File(dataFolder, "login_bonus.yml"))

    fun getAll(): List<LoginBonus> {
        val loginBonusList = mutableListOf<LoginBonus>()

        try {
            val config = storage.loadYamlConfiguration()

            val loginBonusIdSet = config.root?.getKeys(false).orEmpty()

            val list = loginBonusIdSet.map { id ->
                val loginBonusId = LoginBonusId(id)

                return@map LoginBonus(
                        id = loginBonusId,
                        start = LocalDateTime.parse(config.getString("$id.start"), DATE_FORMATTER),
                        end = LocalDateTime.parse(config.getString("$id.end"), DATE_FORMATTER),
                        command = config.getString("$id.command").orEmpty(),
                        message = config.getString("$id.message")
                )
            }

            loginBonusList.addAll(list)
        } catch (e: Exception) {
            Log.error("LoginBonusの読み込みに失敗しました", e)
        }

        return loginBonusList
    }
}
