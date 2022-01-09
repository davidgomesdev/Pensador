package me.l3n.bot.discord.pensador.config

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class ConfigProducer(private val config: Config) {

    @Produces
    fun discord() = config.discord
}