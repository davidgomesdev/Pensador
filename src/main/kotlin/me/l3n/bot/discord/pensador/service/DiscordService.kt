package me.l3n.bot.discord.pensador.service

import dev.kord.core.Kord
import dev.kord.core.behavior.execute
import dev.kord.core.entity.Webhook
import dev.kord.core.event.Event
import io.quarkus.runtime.Startup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.l3n.bot.discord.pensador.config.BotConfiguration
import me.l3n.bot.discord.pensador.config.DiscordConfiguration
import me.l3n.bot.discord.pensador.service.crawler.Quote
import me.l3n.bot.discord.pensador.service.handler.EventHandler
import me.l3n.bot.discord.pensador.util.getTextChannel
import org.jboss.logging.Logger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance
import javax.inject.Inject


@Startup
@ApplicationScoped
class DiscordService(
    private val discord: Kord,
    private val webhook: Webhook,
    private val config: DiscordConfiguration,
    private val botConfig: BotConfiguration,
) {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var eventHandlers: Instance<EventHandler<*>>

    @DelicateCoroutinesApi
    @PostConstruct
    fun startup() {
        registerEvents()

        log.debug("Logging in...")
        GlobalScope.launch(Unconfined) { discord.login() }
    }

    private fun registerEvents() =
        eventHandlers.forEach { handler ->
            handler.register<Event>(discord)
        }

    @DelicateCoroutinesApi
    @PreDestroy
    fun shutdown() {
        log.debug("Logging out...")
        GlobalScope.launch(Unconfined) { discord.logout() }
    }

    suspend fun cleanupFreshQuotes() =
        discord.getTextChannel(config.channelId()).messages.collect { msg -> msg.delete() }

    suspend infix fun sendQuote(quote: Quote) {
        webhook.execute(config.webhook().token()) {
            avatarUrl = quote.author.imageUrl ?: botConfig.noImageUrl()
            username = quote.author.name
            content = quote.text
        }
    }
}

fun Quote.isValid() = text.length < 2_000
