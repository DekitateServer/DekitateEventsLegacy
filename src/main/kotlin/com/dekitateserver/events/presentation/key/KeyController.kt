package com.dekitateserver.events.presentation.key

import com.dekitateserver.events.DekitateEventsPlugin
import com.dekitateserver.events.data.vo.KeyEditType
import com.dekitateserver.events.data.vo.KeyId
import com.dekitateserver.events.domain.usecase.key.*
import com.dekitateserver.events.util.selectPlayersOrError
import com.dekitateserver.events.util.sendWarnMessage
import com.dekitateserver.events.util.toIntOrError
import com.dekitateserver.events.util.toPlayerOrError
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender

class KeyController(plugin: DekitateEventsPlugin) {

    private val server = plugin.server
    private val pluginScope = plugin.pluginScope

    private val useKeyUseCase = UseKeyUseCase(plugin.keyRepository)
    private val giveKeyUseCase = GiveKeyUseCase(plugin.keyRepository)
    private val createKeyUseCase = CreateKeyUseCase(plugin.keyRepository)
    private val deleteKeyUseCase = DeleteKeyUseCase(plugin.keyRepository)
    private val editKeyUseCase = EditKeyUseCase(plugin.keyRepository)
    private val sendKeyEditKeyUseCase = SendKeyEditTypeListUseCase()
    private val sendKeyListUseCase = SendKeyListUseCase(plugin.keyRepository)
    private val sendKeyInfoUseCase = SendKeyInfoUseCase(plugin.keyRepository)
    private val reloadKeyUseCase = ReloadKeyUseCase(plugin.keyRepository)

    fun use(sender: CommandSender, argSelector: String, argKeyId: String) {
        val keyId = KeyId(argKeyId)

        server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
            useKeyUseCase(player, keyId)
        }
    }

    fun give(sender: CommandSender, argSelector: String, argKeyId: String, argAmount: String?) {
        val keyId = KeyId(argKeyId)
        val amount = if (argAmount == null) 1 else argAmount.toIntOrError() ?: return

        server.selectPlayersOrError(sender, argSelector)?.forEach { player ->
            giveKeyUseCase(player, keyId, amount)
        }
    }

    fun create(sender: CommandSender, argKeyId: String) {
        pluginScope.launch {
            createKeyUseCase(
                    player = sender.toPlayerOrError() ?: return@launch,
                    keyId = KeyId(argKeyId)
            )
        }
    }

    fun delete(sender: CommandSender, argKeyId: String) {
        pluginScope.launch {
            deleteKeyUseCase(
                    sender = sender,
                    keyId = KeyId(argKeyId)
            )
        }
    }

    fun edit(sender: CommandSender, argKeyId: String, argType: String, argArgs: List<String>) {
        val keyEditType = KeyEditType.find(argType) ?: let {
            sender.sendWarnMessage("存在しないKeyEditTypeです. [type: $argType]")
            return
        }

        pluginScope.launch {
            editKeyUseCase(
                    sender = sender,
                    keyId = KeyId(argKeyId),
                    type = keyEditType,
                    args = argArgs
            )
        }
    }

    fun sendEditTypeList(sender: CommandSender) {
        sendKeyEditKeyUseCase(sender)
    }

    fun sendList(sender: CommandSender) {
        sendKeyListUseCase(sender)
    }

    fun sendInfo(sender: CommandSender, argKeyId: String) {
        sendKeyInfoUseCase(
                sender = sender,
                keyId = KeyId(argKeyId)
        )
    }

    fun reload() {
        pluginScope.launch {
            reloadKeyUseCase()
        }
    }
}
