package me.l3n.bot.discord.pensador.service

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.execute
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.entity.Webhook
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import dev.kord.core.live.live
import dev.kord.core.live.on
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import io.quarkus.runtime.Startup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.l3n.bot.discord.pensador.config.BotConfig
import me.l3n.bot.discord.pensador.config.DiscordConfig
import me.l3n.bot.discord.pensador.model.Quote
import me.l3n.bot.discord.pensador.repository.QuoteRepository
import me.l3n.bot.discord.pensador.service.handler.EventHandler
import me.l3n.bot.discord.pensador.util.getTextChannel
import me.l3n.bot.discord.pensador.util.isNotMe
import org.jboss.logging.Logger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance
import javax.inject.Inject


val ESCAPE_DISCORD_REGEX = "([*_~`>|])".toRegex()
val FAVORITE_EMOJI = Emojis.heart.toReaction()

@Startup
@ApplicationScoped
class DiscordService(
    private val discord: Kord,
    private val webhook: Webhook,
    private val config: DiscordConfig,
    private val botConfig: BotConfig,
    private val quoteRepository: QuoteRepository,
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

    @KordPreview
    suspend infix fun sendChannelQuote(quote: Quote) {
        val message = webhook.execute(config.webhook().token()) {
            avatarUrl = quote.author.imageUrl ?: botConfig.noImageUrl()
            username = quote.author.name
            content = quote.text.escapeForDiscord()
        }

        message.addReaction(FAVORITE_EMOJI)

        message.live().on<ReactionAddEvent> { event ->
            if (event.emoji == FAVORITE_EMOJI && discord isNotMe event.user)
                quoteRepository.favoriteLast(event.userId.value)
        }
        message.live().on<ReactionRemoveEvent> { event ->
            if (event.emoji == FAVORITE_EMOJI && discord isNotMe event.user)
                quoteRepository.unfavoriteLast(event.userId.value)
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
