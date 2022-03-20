package me.l3n.bot.discord.pensador.producer

import dev.kord.common.entity.WebhookType
import dev.kord.core.Kord
import dev.kord.core.cache.data.WebhookData
import dev.kord.core.entity.Webhook
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.config.DiscordConfig
import me.l3n.bot.discord.pensador.service.discord.util.getTextChannel
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Singleton

@ApplicationScoped
class DiscordProducer(private val config: DiscordConfig) {

    @Singleton
    fun kord() = runBlocking {
        Kord(config.botToken())
    }

    @ApplicationScoped
    fun webhook(kord: Kord) = Webhook(
        WebhookData(
            config.webhook().id(),
            WebhookType.Incoming,
            channelId = config.channelId(),
            applicationId = config.appId(),
        ),
        kord = kord
    )

    @Default
    @ApplicationScoped
    fun infoChannel(kord: Kord) = runBlocking {
        kord.getTextChannel(config.channelId())
    }
}