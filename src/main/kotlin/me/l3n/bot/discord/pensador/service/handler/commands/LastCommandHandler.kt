package me.l3n.bot.discord.pensador.service.handler.commands

import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.x.emoji.Emojis
import me.l3n.bot.discord.pensador.repository.QuoteRepository
import me.l3n.bot.discord.pensador.service.handler.CommandHandler
import me.l3n.bot.discord.pensador.service.replyQuote
import me.l3n.bot.discord.pensador.util.success
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class LastCommandHandler(
    private val quoteRepository: QuoteRepository
) : CommandHandler() {

    override val name = "last"

    @Inject
    private lateinit var log: Logger

    override suspend fun handle(args: List<String>, message: Message): Result<Unit> {
        val author = message.author ?: return Result.failure(IllegalArgumentException("No author!"))
        val searchingMessage = message.reply { content = "Going back in time... ${Emojis.europeanCastle}" }

        log.debug("Getting history for '${author.username}'")

        val lastQuote = quoteRepository.getLast()
            ?: return Result.failure(IllegalStateException("Last command requested with no quotes sent yet!"))

        searchingMessage.delete()
        message.replyQuote(lastQuote)

        return Result.success()
    }
}
