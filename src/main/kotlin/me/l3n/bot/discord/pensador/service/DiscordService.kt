package me.l3n.bot.discord.pensador.service

import dev.kord.core.Kord
import dev.kord.core.behavior.execute
import dev.kord.core.behavior.reply
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
import me.l3n.bot.discord.pensador.service.router.CommandRouter
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
    private val commandRouter: CommandRouter,
) {

    @Inject
    lateinit var log: Logger

    @DelicateCoroutinesApi
    @PostConstruct
    fun startup() {
        discord.on<ReadyEvent> {
            log.info("Logged in!")
        }

        discord.on<MessageCreateEvent> {
            if (this.getGuild() != null) return@on

            val author = message.author ?: return@on

            if (author == discord.getSelf()) return@on

            val username = author.username

            log.debug("Got DM message from '$username'")

            val dmChannel = author.getDmChannelOrNull()

            if (dmChannel == null)
                log.debug("Not allowed to reply")
            else {
                val result = commandRouter routeMessage message

                if (result.isSuccess) {
                    log.info("Command of '$username' routed successfully")
                } else {
                    val error = result.exceptionOrNull() ?: return@on
                    val response = when (error) {
                        is IllegalArgumentException -> """Command not found :frowning2:"""
                        is IllegalStateException -> "Command not working! :worried:"
                        else -> "Internal error :confused:"
                    }

                    message.reply {
                        content = response
                    }
                }
            }
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

    suspend infix fun sendQuote(quote: Quote) {
        webhook.execute(config.webhook().token()) {
            avatarUrl = quote.author.imageUrl ?: NO_AUTHOR_IMAGE
            username = quote.author.name
            content = quote.text
        }
    }
}

fun Quote.isValid() = text.length < 2_000
