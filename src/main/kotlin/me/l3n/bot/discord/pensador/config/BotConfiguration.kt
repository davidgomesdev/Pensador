package me.l3n.bot.discord.pensador.config

import io.smallrye.config.ConfigMapping


@ConfigMapping(prefix = "bot")
interface BotConfiguration {

    fun noImageUrl(): String
}