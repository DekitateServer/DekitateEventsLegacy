package com.dekitateserver.events.config

import com.dekitateserver.core.bukkit.config.BukkitConfigurationAdapter
import com.dekitateserver.core.common.config.BaseConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Configuration(plugin: JavaPlugin) : BaseConfiguration(ConfigKeys, {
    BukkitConfigurationAdapter(File(plugin.dataFolder, "config.yml"))
}) {

    init {
        plugin.saveDefaultConfig()
    }
}
