package me.l3n.bot.discord.pensador.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "pensador")
interface PensadorConfig {
    fun pageCount(): Int
}
