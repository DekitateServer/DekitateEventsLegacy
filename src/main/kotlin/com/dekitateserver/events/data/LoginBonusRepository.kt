package com.dekitateserver.events.data

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.entity.LoginBonus
import com.dekitateserver.events.data.source.LoginBonusYamlSource
import com.dekitateserver.events.data.vo.LoginBonusId
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Collections.synchronizedList


class LoginBonusRepository(plugin: DekitateEventsPlugin) {

    private val loginBonusYamlSource = LoginBonusYamlSource(plugin.dataFolder)

    private val loginBonusCacheList = synchronizedList(mutableListOf<LoginBonus>())

    init {
        createCache()
    }

    fun get(loginBonusId: LoginBonusId): LoginBonus? = loginBonusCacheList.find { it.id == loginBonusId }

    fun getAll(): List<LoginBonus> = loginBonusCacheList

    fun getListByNow(): List<LoginBonus> {
        val now = LocalDateTime.now()

        return loginBonusCacheList.filter {
            it.start.isBefore(now) && it.end.isAfter(now)
        }
    }

    suspend fun refreshCache() = withContext(Dispatchers.IO) {
        loginBonusCacheList.clear()
        createCache()
    }

    private fun createCache() {
        loginBonusCacheList.addAll(
                loginBonusYamlSource.getAll()
        )
        Log.info("${loginBonusCacheList.size}個のLoginBonusを読み込みました")
    }
}
