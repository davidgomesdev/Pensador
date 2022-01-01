package me.l3n.bot.discord.pensador.service.handler.events

import dev.kord.core.event.gateway.ReadyEvent
import me.l3n.bot.discord.pensador.service.handler.EventHandler
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class ReadyEventHandler : EventHandler<ReadyEvent>() {

    override val type = ReadyEvent::class

    @Inject
    private lateinit var log: Logger

    override val handle: suspend ReadyEvent.() -> Unit = {
        log.info("Logged in!")
    }
}
