package me.l3n.bot.discord.pensador.service.discord.handler.commands

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.reply
import dev.kord.x.emoji.Emojis
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import me.l3n.bot.discord.pensador.repository.QuoteRepository
import me.l3n.bot.discord.pensador.service.discord.createMessageWithEmbed
import me.l3n.bot.discord.pensador.service.discord.handler.CommandContext
import me.l3n.bot.discord.pensador.service.discord.handler.CommandHandler
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

    override suspend fun handle(args: List<String>, context: CommandContext): Result<Unit> = context.run {
        log.debug("Getting favorite quotes of '${user.username}'")

        val lastFavorites = quoteRepository.getFavorites(user.id.value)

        lastFavorites
            .map(::createMessageWithEmbed)
            .collect { channel.createMessage(it) }

        if (lastFavorites.lastOrNull() == null)
            message.reply { content = "You've got nothing ${Emojis.brokenHeart}" }

        return@run Result.success()
    }
}
