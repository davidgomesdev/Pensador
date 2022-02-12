package me.l3n.bot.discord.pensador.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "mongodb")
interface MongoConfig {

    fun connectionString(): String

    fun database(): String
}