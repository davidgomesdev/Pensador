package me.l3n.bot.discord.pensador.config

import io.smallrye.config.ConfigMapping
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

@ConfigMapping(prefix = "url")
interface UrlConfig {
    fun goodreads(): GoodReadsUrlConfig
    fun pensador(): PensadorUrlConfig
}

interface GoodReadsUrlConfig {
    fun base(): String
    fun quotes(): String
}

interface PensadorUrlConfig {
    fun base(): String
    fun quotes(): String
}

@ApplicationScoped
class UrlConfigProducer(private val urlConfig: UrlConfig) {
    @Produces
    fun goodreads() = urlConfig.goodreads()

    @Produces
    fun pensador() = urlConfig.pensador()
}
