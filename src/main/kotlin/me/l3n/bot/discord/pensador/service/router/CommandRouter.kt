package me.l3n.bot.discord.pensador.service.router


import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import me.l3n.bot.discord.pensador.service.handler.CommandHandler
import me.l3n.bot.discord.pensador.util.success
import org.jboss.logging.Logger
import javax.enterprise.inject.Instance
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CommandRouter(
    private val handlers: Instance<CommandHandler>,
) {

    @Inject
    private lateinit var log: Logger

    suspend fun routeMessage(message: Message, user: User): Result<Unit> {
        val content = message.content
        val commandName = content.commandName
        val args = content.args

        val handler = handlers.firstOrNull { it.name.equals(commandName, true) }

        if (handler == null) {
            log.info("No command handler for '$commandName'")

            return Result.failure(IllegalArgumentException("Provided command is invalid"))
        }

        val replyMessage = handler.handle(args, message, user)

        if (replyMessage.isSuccess) {
            log.info("Command '$commandName' handled succeeded")
        } else {
            val ex = replyMessage.exceptionOrNull()

            log.error("Command '$commandName' failed with args '${args.joinToString()}'",
                replyMessage.exceptionOrNull())

            return Result
                .failure(ex ?: IllegalStateException("Command '$commandName' failed without exception"))
        }

        return Result.success()
    }
}

private var String.args: List<String>
    get() = split(" ").drop(1)
    private set(_) {}

private var String.commandName: String
    get() = split(" ").first()
    private set(_) {}
