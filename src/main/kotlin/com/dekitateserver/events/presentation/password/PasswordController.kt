package com.dekitateserver.events.presentation.password

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.PasswordId
import com.dekitateserver.events.domain.usecase.password.InputPasswordUseCase
import com.dekitateserver.events.util.selectPlayersOrError
import org.bukkit.command.CommandSender

class PasswordController(plugin: DekitateEventsPlugin) {

    private val server = plugin.server

    private val inputPasswordUseCase = InputPasswordUseCase(plugin.passwordRepository)

    fun input(sender: CommandSender, argSelector: String, argPasswordId: String, argText: String) {
        val passwordId = PasswordId(argPasswordId)

        server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
            inputPasswordUseCase(player, passwordId, argText)
        }
    }

    fun set(argsPasswordId: String, argText: String) {

    }

    fun reset(sender: CommandSender, argSelector: String, argPasswordId: String) {

    }

    fun create(sender: CommandSender, argPasswordId: String) {

    }

    fun delete(sender: CommandSender, argPasswordId: String) {

    }

    fun edit(sender: CommandSender, argPasswordId: String, argType: String, argArgs: List<String>) {

    }

    fun sendEditTypeList(sender: CommandSender) {

    }

    fun sendList(sender: CommandSender) {

    }

    fun sendInfo(sender: CommandSender, argPasswordId: String) {

    }

    fun reload() {

    }
}
