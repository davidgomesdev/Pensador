package me.l3n.bot.discord.pensador.service.handler.commands

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.x.emoji.Emojis
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.lastOrNull
import me.l3n.bot.discord.pensador.repository.QuoteRepository
import me.l3n.bot.discord.pensador.service.createMessageWithEmbed
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

    override suspend fun handle(args: List<String>, message: Message, user: User): Result<Unit> {
        val searchingMessage =
            message.reply { content = "Going back to the wonderland... ${Emojis.smilingFaceWith3Hearts}" }

        log.debug("Getting last favorite for '${user.username}'")

        val lastFavorites = quoteRepository.getFavorites(user.id.value)

        searchingMessage.delete()

        lastFavorites
            .collectIndexed { i, quote ->
                if (i == 0) message.replyQuote(quote)
                else user.getDmChannel().createMessage(createMessageWithEmbed(quote))
            }

        if (lastFavorites.lastOrNull() == null)
            message.reply { content = "You've got nothing ${Emojis.brokenHeart}" }

        return Result.success()
    }
}
