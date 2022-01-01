package me.l3n.bot.discord.pensador.service.handler.events

import dev.kord.core.Kord
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import me.l3n.bot.discord.pensador.service.handler.EventHandler
import me.l3n.bot.discord.pensador.service.router.CommandRouter
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class MessageCreateEventHandler(
    private val discord: Kord,
    private val commandRouter: CommandRouter,
) : EventHandler<MessageCreateEvent>() {

    override val type = MessageCreateEvent::class

    @Inject
    private lateinit var log: Logger

    override val handle: suspend MessageCreateEvent.() -> Unit = handler@{
        if (getGuild() != null) return@handler

        val author = message.author ?: return@handler

        if (author == discord.getSelf()) return@handler

        val username = author.username

        log.debug("Got DM message from '$username'")

        val dmChannel = author.getDmChannelOrNull()

        if (dmChannel == null)
            log.debug("Not allowed to reply")
        else {
            val result = commandRouter routeMessage message

            if (result.isSuccess) {
                log.info("Command of '$username' routed successfully")
            } else {
                val error = result.exceptionOrNull() ?: return@handler
                val response = when (error) {
                    is IllegalArgumentException -> """Command not found :frowning2:"""
                    is IllegalStateException -> "Command not working! :worried:"
                    else -> "Internal error :confused:"
                }

                message.reply {
                    content = response
                }
            }
        }
    }
}