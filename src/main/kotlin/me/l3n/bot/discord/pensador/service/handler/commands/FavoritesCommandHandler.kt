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
class FavoritesCommandHandler(
    private val quoteRepository: QuoteRepository
) : CommandHandler() {

    override val name = "favorites"

    @Inject
    private lateinit var log: Logger

    override suspend fun handle(args: List<String>, message: Message): Result<Unit> {
        val author = message.author ?: return Result.failure(IllegalArgumentException("No author!"))
        val searchingMessage =
            message.reply { content = "Going back to the wonderland... ${Emojis.smilingFaceWith3Hearts}" }

        log.debug("Getting last favorite for '${author.username}'")

        val lastFavorite = quoteRepository.getFavorite(author.id.value)

        searchingMessage.delete()

        if (lastFavorite == null)
            message.reply { content = "You've got nothing ${Emojis.brokenHeart}" }
        else
            message.replyQuote(lastFavorite)

        return Result.success()
    }
}
