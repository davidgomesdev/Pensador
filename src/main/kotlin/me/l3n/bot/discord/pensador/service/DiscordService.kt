package me.l3n.bot.discord.pensador.service

import dev.kord.core.Kord
import dev.kord.core.behavior.execute
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.entity.Webhook
import io.quarkus.runtime.Startup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.l3n.bot.discord.pensador.config.BotConfig
import me.l3n.bot.discord.pensador.config.DiscordConfig
import me.l3n.bot.discord.pensador.model.Quote
import me.l3n.bot.discord.pensador.service.handler.EventHandler
import me.l3n.bot.discord.pensador.util.getTextChannel
import org.jboss.logging.Logger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance
import javax.inject.Inject


val ESCAPE_DISCORD_REGEX = "([*_~`>|])".toRegex()

@Startup
@ApplicationScoped
class DiscordService(
    private val discord: Kord,
    private val webhook: Webhook,
    private val config: DiscordConfig,
    private val botConfig: BotConfig,
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

    private fun registerEvents() = eventHandlers.forEach { handler ->
        handler.register(discord)
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
            content = quote.text.escapeForDiscord()
        }
    }
}

fun String.escapeForDiscord(): String = trim().replace(ESCAPE_DISCORD_REGEX, "\\\\$1")

fun Quote.isValid() = text.escapeForDiscord().length < 2_000 && author.name.length < 80

suspend fun Message.replyQuote(quote: Quote) = reply {
    val quoteAuthor = quote.author

    this.embed {
        description = quote.text

        thumbnail {
            this.url = quoteAuthor.imageUrl ?: ""
        }

        footer {
            this.text = quote.author.name
        }
    }
}
