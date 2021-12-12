package me.l3n.bot.discord.pensador.service

import dev.kord.core.Kord
import dev.kord.core.behavior.execute
import dev.kord.core.entity.Webhook
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.config.DiscordConfiguration
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DiscordService(private val kord: Kord, private val webhook: Webhook, private val config: DiscordConfiguration) {

    @PostConstruct
    suspend fun login() = kord.login()

    @PreDestroy
    fun logout() = runBlocking { kord.logout() }

    suspend fun sendMessageAs(message: Message) {
        webhook.execute(config.webhook().token()) {
            avatarUrl = message.avatarUrl
            username = message.username
            content = message.text
        }
    }

    suspend fun sendQuote(quote: Quote) {
        sendMessageAs(Message(quote.author.name,
            quote.author.imageUrl
                ?: "https://thumbs.dreamstime.com/b/em-inc%C3%B3gnito-%C3%ADcone-equipe-cara-com-vidros-barba-e-chap%C3%A9u-suportes-da-foto-vetor-109640094.jpg",
            quote.text))
    }
}

data class Message(val username: String, val avatarUrl: String, val text: String)
