package com.dekitateserver.events.data.entity

import com.dekitateserver.events.domain.vo.MaterialLocation
import com.dekitateserver.events.domain.vo.PasswordId
import com.dekitateserver.events.domain.vo.PasswordInputResult
import org.bukkit.Location
import org.bukkit.Material

data class Password(
        val id: PasswordId,
        private var value: String,
        val blockMaterial: Material? = null,
        val blockLocation: Location? = null,
        val matchMessage: String? = "§b一致",
        val notMatchMessage: String? = "§c不一致",
        val inputMessage: String? = "現在の入力> {buff}",
        val resetMessage: String? = "入力をリセットしました"
) {
    val blockMaterialLocation = if (blockMaterial != null && blockLocation != null) {
        MaterialLocation(blockMaterial, blockLocation)
    } else null

    private val buffer = StringBuilder()
    val bufferText: String
        get() = buffer.toString()

    fun input(text: String): PasswordInputResult {
        if (value.isEmpty()) { // password not set
            return PasswordInputResult.NOT_MATCH
        }

        // clear previous buffer if needed
        if (buffer.length >= value.length) {
            buffer.setLength(0)
        }

        buffer.append(text)

        return when {
            buffer.length < value.length -> PasswordInputResult.CONTINUE
            value == buffer.toString() -> PasswordInputResult.MATCH
            else -> PasswordInputResult.NOT_MATCH
        }
    }

    fun get(): String = value

    fun set(password: String) {
        value = password
    }

    fun reset() {
        buffer.setLength(0)
    }
}
