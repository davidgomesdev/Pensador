package me.l3n.bot.discord.pensador.service.discord.handler.events

import dev.kord.core.Kord
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.on
import me.l3n.bot.discord.pensador.service.discord.handler.EventHandler
import org.jboss.logging.Logger
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class UserCloseEvent : EventHandler {

    @Inject
    private lateinit var log: Logger

    override fun register(discord: Kord) {
        discord.on<DisconnectEvent.UserCloseEvent> { log.info("Logged out!") }
    }
}