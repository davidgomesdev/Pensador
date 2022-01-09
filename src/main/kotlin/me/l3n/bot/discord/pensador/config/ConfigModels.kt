package me.l3n.bot.discord.pensador.config

import dev.kord.common.entity.Snowflake


data class Config(
    val discord: DiscordConfig,
)

data class DiscordConfig(
    val botToken: String,
    val webhook: WebhookConfig,
    val channelId: Snowflake,
    val appId: Snowflake,
)

data class WebhookConfig(
    val id: Snowflake,
    val token: String,
)
