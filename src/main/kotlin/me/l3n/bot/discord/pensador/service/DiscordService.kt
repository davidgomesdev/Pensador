package me.l3n.bot.discord.pensador.service

import dev.kord.core.Kord
import dev.kord.core.behavior.execute
import dev.kord.core.entity.Webhook
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.l3n.bot.discord.pensador.config.DiscordConfiguration
import me.l3n.bot.discord.pensador.util.getTextChannel
import org.jboss.logging.Logger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


const val NO_AUTHOR_IMAGE =
    "https://thumbs.dreamstime.com/b/em-inc%C3%B3gnito-%C3%ADcone-equipe-cara-com-vidros-barba-e-chap%C3%A9u-suportes-da-foto-vetor-109640094.jpg"

@ApplicationScoped
class DiscordService(
    private val kord: Kord,
    private val webhook: Webhook,
    private val config: DiscordConfiguration,
) {

    @Inject
    lateinit var log: Logger

    @DelicateCoroutinesApi
    @PostConstruct
    fun login() {
        GlobalScope.launch(Unconfined) { kord.login() }
    }

    @DelicateCoroutinesApi
    @PreDestroy
    fun logout() {
        GlobalScope.launch(Unconfined) { kord.logout() }
    }

    suspend fun cleanupFreshQuotes() =
        kord.getTextChannel(config.channelId()).messages.collect { msg -> msg.delete() }

    suspend fun sendMessageAs(message: Message) {
        webhook.execute(config.webhook().token()) {
            avatarUrl = message.avatarUrl
            username = message.username
            content = message.text
        }
    }

    suspend fun sendQuote(quote: Quote) {
        sendMessageAs(quote.toMessage())
    }
}

data class Message(val username: String, val avatarUrl: String, val text: String)

fun Quote.toMessage() = Message(author.name, author.imageUrl ?: NO_AUTHOR_IMAGE, text)
