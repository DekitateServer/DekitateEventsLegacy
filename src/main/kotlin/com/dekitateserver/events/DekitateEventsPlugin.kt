package com.dekitateserver.events

import com.dekitateserver.core.DekitatePlugin
import com.dekitateserver.events.config.ConfigKeys
import com.dekitateserver.events.config.Configuration
import com.dekitateserver.events.data.*
import com.dekitateserver.events.data.vo.GachaCost
import com.dekitateserver.events.presentation.event.EventCommand
import com.dekitateserver.events.presentation.event.EventController
import com.dekitateserver.events.presentation.gacha.GachaCommand
import com.dekitateserver.events.presentation.gacha.GachaController
import com.dekitateserver.events.presentation.gacha.GachaEventListener
import com.dekitateserver.events.presentation.key.KeyCommand
import com.dekitateserver.events.presentation.key.KeyController
import com.dekitateserver.events.presentation.loginbonus.LoginBonusCommand
import com.dekitateserver.events.presentation.loginbonus.LoginBonusController
import com.dekitateserver.events.presentation.loginbonus.LoginBonusEventListener
import com.dekitateserver.events.presentation.password.PasswordCommand
import com.dekitateserver.events.presentation.password.PasswordController
import com.dekitateserver.events.presentation.spawn.SpawnController
import com.dekitateserver.events.presentation.spawn.SpawnEventListener
import com.dekitateserver.events.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.mariadb.jdbc.MariaDbPoolDataSource
import javax.sql.DataSource

class DekitateEventsPlugin : DekitateEvents, DekitatePlugin() {

    private lateinit var mariaDbPoolDataSource: MariaDbPoolDataSource
    internal val dataSource: DataSource
        get() = mariaDbPoolDataSource

    lateinit var signMetaRepository: SignMetaRepository
    lateinit var gachaRepository: GachaRepository
    lateinit var keyRepository: KeyRepository
    lateinit var passwordRepository: PasswordRepository
    lateinit var loginBonusRepository: LoginBonusRepository
    lateinit var eventTicketHistoryRepository: EventTicketHistoryRepository
    lateinit var voteTicketHistoryRepository: VoteTicketHistoryRepository
    lateinit var gachaHistoryRepository: GachaHistoryRepository
    lateinit var loginBonusHistoryRepository: LoginBonusHistoryRepository

    override fun onEnable() {
        super.onEnable()

        Log.init(logger, server)
        logger.info("Enabled.")

        val configuration = Configuration(this)
        configuration.load()

        mariaDbPoolDataSource = createMariaDbPoolDataSource(
                url = configuration.get(ConfigKeys.DATABASE_URL),
                user = configuration.get(ConfigKeys.DATABASE_USER),
                password = configuration.get(ConfigKeys.DATABASE_PASSWORD)
        )

        signMetaRepository = SignMetaRepository(this)
        gachaRepository = GachaRepository(this)
        keyRepository = KeyRepository(this)
        passwordRepository = PasswordRepository(this)
        loginBonusRepository = LoginBonusRepository(this)
        eventTicketHistoryRepository = EventTicketHistoryRepository(this)
        voteTicketHistoryRepository = VoteTicketHistoryRepository(this)
        gachaHistoryRepository = GachaHistoryRepository(this)
        loginBonusHistoryRepository = LoginBonusHistoryRepository(this)

        val eventController = EventController(this)
        val gachaController = GachaController(this)
        val keyController = KeyController(this)
        val passwordController = PasswordController(this)
        val spawnController = SpawnController(this)
        val loginBonusController = LoginBonusController(this)

        registerCommand(EventCommand(eventController))
        registerCommand(GachaCommand(gachaController))
        registerCommand(KeyCommand(keyController))
        registerCommand(PasswordCommand(passwordController))
        registerCommand(LoginBonusCommand(loginBonusController))

        server.pluginManager.registerEvents(GachaEventListener(gachaController), this)
        server.pluginManager.registerEvents(SpawnEventListener(spawnController), this)
        server.pluginManager.registerEvents(LoginBonusEventListener(loginBonusController), this)

        pluginScope.launch {
            logger.info("current thread1: ${Thread.currentThread().name}}")

            withContext(Dispatchers.IO) {
                logger.info("current thread2: ${Thread.currentThread().name}}")
            }
        }
    }

    override fun onDisable() {
        mariaDbPoolDataSource.close()

        super.onDisable()
    }

    override fun onLoad() {
        super.onLoad()

        /**
         * GachaCost
         */
        ConfigurationSerialization.registerClass(GachaCost.Free::class.java)
        ConfigurationSerialization.registerClass(GachaCost.EventTicket::class.java)
        ConfigurationSerialization.registerClass(GachaCost.VoteTicket::class.java)
        ConfigurationSerialization.registerClass(GachaCost.Key::class.java)
    }

    private fun createMariaDbPoolDataSource(url: String, user: String, password: String) = MariaDbPoolDataSource(url).apply {
        poolName = "dekitate_events"
        setUser(user)
        setPassword(password)
    }
}
