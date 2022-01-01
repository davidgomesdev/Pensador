package me.l3n.bot.discord.pensador.service.handler.events

import dev.kord.core.event.gateway.DisconnectEvent
import me.l3n.bot.discord.pensador.service.handler.EventHandler
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class DisconnectEventHandler : EventHandler<DisconnectEvent>() {

    override val type = DisconnectEvent::class

    @Inject
    private lateinit var log: Logger

    override val handle: suspend DisconnectEvent.() -> Unit = {
        log.info("Logged out!")
    }
}