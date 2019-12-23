package com.dekitateserver.events.util

import org.bukkit.Server
import java.util.logging.Level
import java.util.logging.Logger

object Log {

    private const val PREFIX_INFO = "§7Events§fINFO§r "
    private const val PREFIX_WARN = "§7Events§eWARN§r "
    private const val PREFIX_ERROR = "§7Events§4ERROR§r "

    private lateinit var logger: Logger
    private lateinit var server: Server

    fun init(logger: Logger, server: Server) {
        this.logger = logger
        this.server = server
    }

    fun info(message: String) {
        logger.info(message)
        sendMessageToOps(PREFIX_INFO + message)
    }

    fun warn(message: String) {
        logger.warning(message)
        sendMessageToOps(PREFIX_WARN + message)
    }

    fun error(message: String, thrown: Throwable? = null) {
        if (thrown == null) {
            logger.log(Level.SEVERE, message)
            sendMessageToOps(PREFIX_ERROR + message)
        } else {
            logger.log(Level.SEVERE, message, thrown)

            thrown.message?.let {
                sendMessageToOps(PREFIX_ERROR + it)
            }
            sendMessageToOps(PREFIX_ERROR + message)
        }
    }

    private fun sendMessageToOps(message: String) {
        server.onlinePlayers.forEach {
            if (it.isOp) {
                it.sendMessage(message)
            }
        }
    }
}