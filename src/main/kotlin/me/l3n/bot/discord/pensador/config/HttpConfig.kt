package me.l3n.bot.discord.pensador.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "http")
interface HttpConfig {

    fun quotesUrl(): String
}
