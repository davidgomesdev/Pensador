package me.l3n.bot.discord.pensador.service.discord.handler.events

import dev.kord.core.event.gateway.DisconnectEvent
import me.l3n.bot.discord.pensador.service.discord.handler.EventHandler
import org.jboss.logging.Logger
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class UserCloseEvent : EventHandler<DisconnectEvent.UserCloseEvent>(DisconnectEvent.UserCloseEvent::class) {

    @Inject
    private lateinit var log: Logger

    override val handler: suspend DisconnectEvent.UserCloseEvent.() -> Unit = {
        log.info("Logged out!")
    }
}