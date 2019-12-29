package com.dekitateserver.events.infrastructure.repository

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.domain.entity.LoginBonus
import com.dekitateserver.events.domain.repository.LoginBonusRepository
import com.dekitateserver.events.domain.vo.LoginBonusId
import com.dekitateserver.events.infrastructure.source.LoginBonusYamlSource
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Collections.synchronizedList

class LoginBonusRepositoryImpl(plugin: DekitateEventsPlugin) : LoginBonusRepository {

    private val loginBonusYamlSource = LoginBonusYamlSource(plugin.dataFolder)

    private val loginBonusCacheList = synchronizedList(mutableListOf<LoginBonus>())

    init {
        createCache()
    }

    override fun get(loginBonusId: LoginBonusId): LoginBonus? = loginBonusCacheList.find { it.id == loginBonusId }

    override fun getListByNow(): List<LoginBonus> {
        val now = LocalDateTime.now()

        return loginBonusCacheList.filter {
            it.start.isBefore(now) && it.end.isAfter(now)
        }
    }

    override fun getAll(): List<LoginBonus> = loginBonusCacheList

    override suspend fun refreshCache() = withContext(Dispatchers.IO) {
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
