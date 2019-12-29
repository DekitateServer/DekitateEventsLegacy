package com.dekitateserver.events.domain.vo

import org.bukkit.Location
import org.bukkit.Material

data class MaterialLocation(
        val material: Material,
        val location: Location
)
