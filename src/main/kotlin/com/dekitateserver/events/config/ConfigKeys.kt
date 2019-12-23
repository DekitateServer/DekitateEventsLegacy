package com.dekitateserver.events.config

import com.dekitateserver.core.config.AbstractConfigKeys

object ConfigKeys : AbstractConfigKeys() {
    val DATABASE_URL = stringKey("database.url", "")
    val DATABASE_USER = stringKey("database.user", "")
    val DATABASE_PASSWORD = stringKey("database.password", "")

    init {
        initKeyMap()
    }
}
