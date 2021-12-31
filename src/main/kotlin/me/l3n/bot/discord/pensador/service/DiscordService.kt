package me.l3n.bot.discord.pensador.service

import dev.kord.core.Kord
import dev.kord.core.behavior.execute
import dev.kord.core.entity.Webhook
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.quarkus.runtime.Startup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.l3n.bot.discord.pensador.config.DiscordConfiguration
import me.l3n.bot.discord.pensador.service.crawler.Quote
import me.l3n.bot.discord.pensador.util.getTextChannel
import org.jboss.logging.Logger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


const val NO_AUTHOR_IMAGE =
    "https://thumbs.dreamstime.com/b/em-inc%C3%B3gnito-%C3%ADcone-equipe-cara-com-vidros-barba-e-chap%C3%A9u-suportes-da-foto-vetor-109640094.jpg"

@Startup
@ApplicationScoped
class DiscordService(
    private val discord: Kord,
    private val webhook: Webhook,
    private val config: DiscordConfiguration,
) {

    @Inject
    lateinit var log: Logger

    @DelicateCoroutinesApi
    @PostConstruct
    fun startup() {
        discord.on<ReadyEvent> {
            log.info("Logged in!")
        }

        log.debug("Logging in...")
        GlobalScope.launch(Unconfined) { discord.login() }
    }

    @DelicateCoroutinesApi
    @PreDestroy
    fun shutdown() {
        discord.on<DisconnectEvent> {
            log.info("Logged out!")
        }

        log.debug("Logging out...")
        GlobalScope.launch(Unconfined) { discord.logout() }
    }

    suspend fun cleanupFreshQuotes() =
        discord.getTextChannel(config.channelId()).messages.collect { msg -> msg.delete() }

    private suspend fun sendWebhookMessage(message: Message) {
        webhook.execute(config.webhook().token()) {
            avatarUrl = message.avatarUrl
            username = message.username
            content = message.text
        }
    }

    suspend infix fun sendQuote(quote: Quote) {
        if (!quote.isValid())
            throw IllegalArgumentException("Quote not valid")

        sendWebhookMessage(quote.toMessage())
    }
}

data class Message(val username: String, val avatarUrl: String, val text: String)

fun Quote.toMessage() = Message(author.name, author.imageUrl ?: NO_AUTHOR_IMAGE, text)

fun Quote.isValid() = text.length < 2_000
