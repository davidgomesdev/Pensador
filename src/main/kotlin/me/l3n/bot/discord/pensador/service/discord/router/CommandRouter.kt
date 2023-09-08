package me.l3n.bot.discord.pensador.service.discord.router


import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import me.l3n.bot.discord.pensador.service.discord.handler.CommandContext
import me.l3n.bot.discord.pensador.service.discord.handler.CommandHandler
import me.l3n.bot.discord.pensador.util.success
import org.jboss.logging.Logger
import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class CommandRouter(
    private val handlers: Instance<CommandHandler>,
) {

    @Inject
    private lateinit var log: Logger

    suspend fun routeMessage(message: Message, user: User): Result<Unit> {
        val content = message.content
        val commandName = content.commandName
        val channel = user.getDmChannelOrNull() ?: return Result.success()
        val args = content.args

        val handler = handlers.firstOrNull { it.name.equals(commandName, true) }

        if (handler == null) {
            log.info("No command handler for '$commandName'")

            return Result.failure(IllegalArgumentException("Provided command is invalid"))
        }

        val replyMessage = handler.handle(args, CommandContext(message, user, channel))

        return replyMessage.fold(
            onSuccess = {
                log.info("Command '$commandName' handled succeeded")
                Result.success()
            },
            onFailure = { ex ->
                log.error(
                    "Command '$commandName' failed with args '${args.joinToString()}'",
                    ex
                )

                Result.failure(ex)
            }
        )
    }
}

private var String.args: List<String>
    get() = split(" ").drop(1)
    private set(_) {}

private var String.commandName: String
    get() = split(" ").first()
    private set(_) {}
