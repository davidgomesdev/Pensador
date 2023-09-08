package me.l3n.bot.discord.pensador.service.discord.handler.events

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.x.emoji.Emojis
import me.l3n.bot.discord.pensador.service.discord.handler.EventHandler
import me.l3n.bot.discord.pensador.service.discord.router.CommandRouter
import org.jboss.logging.Logger
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class MessageCreateEventHandler(
    private val discord: Kord,
    private val commandRouter: CommandRouter,
) : EventHandler<MessageCreateEvent>(MessageCreateEvent::class) {

    @Inject
    private lateinit var log: Logger

    override val handler: suspend MessageCreateEvent.() -> Unit = handler@{
        if (getGuildOrNull() != null) return@handler

        val author = message.author ?: return@handler

        if (author == discord.getSelf()) return@handler

        val username = author.username

        log.debug("Got DM message from '$username'")

        val dmChannel = author.getDmChannelOrNull()

        if (dmChannel == null) log.debug("Not allowed to reply")
        else {
            dmChannel.withTyping {
                val result = commandRouter.routeMessage(message, author)

                result.fold(
                    onSuccess = {
                        log.info("Command of '$username' routed successfully")
                    },
                    onFailure = { ex ->
                        val response = getFailureResponse(ex)

                        message.reply {
                            content = response
                        }
                    },
                )
            }
        }
    }

    private fun getFailureResponse(ex: Throwable) = when (ex) {
        is IllegalArgumentException -> "Command not found ${Emojis.frowning2}"
        is IllegalStateException -> "Command not working! ${Emojis.worried}"
        else -> "Internal error ${Emojis.confused}"
    }
}