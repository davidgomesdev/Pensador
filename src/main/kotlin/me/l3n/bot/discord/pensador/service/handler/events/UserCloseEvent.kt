package me.l3n.bot.discord.pensador.service.handler.events

import dev.kord.core.event.gateway.DisconnectEvent
import me.l3n.bot.discord.pensador.service.handler.EventHandler
import org.jboss.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserCloseEvent : EventHandler<DisconnectEvent.UserCloseEvent>() {

    override val type = DisconnectEvent.UserCloseEvent::class

    @Inject
    private lateinit var log: Logger

    override val handle: suspend DisconnectEvent.UserCloseEvent.() -> Unit = {
        log.info("Logged out!")
    }
}