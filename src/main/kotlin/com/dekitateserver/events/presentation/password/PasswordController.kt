package com.dekitateserver.events.presentation.password

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.PasswordId
import com.dekitateserver.events.domain.usecase.password.*
import com.dekitateserver.events.util.selectPlayersOrError
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender

class PasswordController(plugin: DekitateEventsPlugin) {

    private val server = plugin.server
    private val pluginScope = plugin.pluginScope

    private val inputPasswordUseCase = InputPasswordUseCase(plugin.passwordRepository)
    private val setPasswordUseCase = SetPasswordUseCase(plugin.passwordRepository)
    private val resetPasswordUseCase = ResetPasswordUseCase(plugin.passwordRepository)
    private val createPasswordUseCase = CreatePasswordUseCase(plugin.passwordRepository)
    private val deletePasswordUseCase = DeletePasswordUseCase(plugin.passwordRepository)
    private val sendPasswordEditTypeListUseCase = SendPasswordEditTypeListUseCase()
    private val sendPasswordListUseCase = SendPasswordListUseCase(plugin.passwordRepository)
    private val sendPasswordInfoUseCase = SendPasswordInfoUseCase(plugin.passwordRepository)
    private val reloadPasswordUseCase = ReloadPasswordUseCase(plugin.passwordRepository)

    fun input(sender: CommandSender, argSelector: String, argPasswordId: String, argText: String) {
        val passwordId = PasswordId(argPasswordId)

        server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
            inputPasswordUseCase(player, passwordId, argText)
        }
    }

    fun set(argsPasswordId: String, argText: String) {
        pluginScope.launch {
            setPasswordUseCase(
                    passwordId = PasswordId(argsPasswordId),
                    text = argText
            )
        }
    }

    fun reset(sender: CommandSender, argSelector: String, argPasswordId: String) {
        val passwordId = PasswordId(argPasswordId)

        server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
            resetPasswordUseCase(player, passwordId)
        }
    }

    fun create(sender: CommandSender, argPasswordId: String) {
        pluginScope.launch {
            createPasswordUseCase(
                    sender = sender,
                    passwordId = PasswordId(argPasswordId)
            )
        }
    }

    fun delete(sender: CommandSender, argPasswordId: String) {
        pluginScope.launch {
            deletePasswordUseCase(
                    sender = sender,
                    passwordId = PasswordId(argPasswordId)
            )
        }
    }

    fun edit(sender: CommandSender, argPasswordId: String, argType: String, argArgs: List<String>) {

    }

    fun sendEditTypeList(sender: CommandSender) {
        sendPasswordEditTypeListUseCase(sender)
    }

    fun sendList(sender: CommandSender) {
        sendPasswordListUseCase(sender)
    }

    fun sendInfo(sender: CommandSender, argPasswordId: String) {
        sendPasswordInfoUseCase(
                sender = sender,
                passwordId = PasswordId(argPasswordId)
        )
    }

    fun reload() {
        pluginScope.launch {
            reloadPasswordUseCase()
        }
    }
}
