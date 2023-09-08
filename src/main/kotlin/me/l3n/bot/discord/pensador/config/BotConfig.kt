package me.l3n.bot.discord.pensador.config

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault


@ConfigMapping(prefix = "bot")
interface BotConfig {

    fun noImageUrl(): String

    @WithDefault("2000")
    fun charLimit(): Int

    /**
     * Valid styles: webhook, embed
      */
    @WithDefault("webhook")
    fun channelMessageType(): String
}