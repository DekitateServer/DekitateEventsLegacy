package com.dekitateserver.events.data.vo

import org.bukkit.configuration.serialization.ConfigurationSerializable

enum class ParkourAction : ConfigurationSerializable {
    JOIN,
    START,
    END,
    EXIT;

    override fun serialize() = mapOf("name" to name)

    companion object {
        @Suppress("UNUSED")
        @JvmStatic
        fun deserialize(map: Map<String, Any?>) = valueOf(map["name"] as String)
    }
}
