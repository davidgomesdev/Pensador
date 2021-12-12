package me.l3n.bot.discord.pensador.config

import dev.kord.common.entity.Snowflake
import io.smallrye.config.ConfigMapping


@ConfigMapping(prefix = "discord")
interface DiscordConfiguration {

    fun botToken(): String
    fun webhook(): WebhookConfiguration
    fun channelId(): Snowflake
    fun appId(): Snowflake

    interface WebhookConfiguration {
        fun id(): Snowflake
        fun token(): String
    }
}
