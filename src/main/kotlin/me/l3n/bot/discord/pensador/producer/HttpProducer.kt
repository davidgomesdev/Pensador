package me.l3n.bot.discord.pensador.producer

import io.ktor.client.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

@ApplicationScoped
class HttpProducer {

    @Produces
    fun client() = HttpClient()
}