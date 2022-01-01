package me.l3n.bot.discord.pensador.producer

import dev.kord.common.entity.WebhookType
import dev.kord.core.Kord
import dev.kord.core.cache.data.WebhookData
import dev.kord.core.entity.Webhook
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.config.DiscordConfiguration
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces
import javax.inject.Singleton

@ApplicationScoped
class DiscordProducer(private val config: DiscordConfiguration) {

    @Produces
    @Singleton
    fun kord(): Kord = runBlocking {
        Kord(config.botToken())
    }

    @Produces
    fun webhook(kord: Kord): Webhook = Webhook(
        WebhookData(
            config.webhook().id(),
            WebhookType.Incoming,
            channelId = config.channelId(),
            applicationId = config.appId(),
        ),
        kord = kord
    )
}