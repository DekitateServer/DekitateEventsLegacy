package com.dekitateserver.events.config

import com.dekitateserver.core.config.AbstractConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Configuration(plugin: JavaPlugin) : AbstractConfiguration(ConfigKeys, File(plugin.dataFolder, "config.yml")) {
    init {
        plugin.saveDefaultConfig()
    }
}
