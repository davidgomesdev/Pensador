package me.l3n.bot.discord.pensador.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "goodreads")
interface GoodReadsConfig {
    fun pageCount(): Int
}
