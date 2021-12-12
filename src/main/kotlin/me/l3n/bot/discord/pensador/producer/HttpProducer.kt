package me.l3n.bot.discord.pensador.producer

import io.ktor.client.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class HttpProducer {

    @Produces
    fun client() = HttpClient()
}