package me.l3n.bot.discord.pensador.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "url.pensador")
interface PensadorUrlConfig {

    fun base(): String
    fun populares(): String
}