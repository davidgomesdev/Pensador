package me.l3n.bot.discord.pensador.service.handler.commands

import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.x.emoji.Emojis
import me.l3n.bot.discord.pensador.config.BotConfig
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.handler.CommandHandler
import me.l3n.bot.discord.pensador.service.replyQuote
import me.l3n.bot.discord.pensador.util.success
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance
import javax.inject.Inject


@ApplicationScoped
class GetQuoteCommandHandler(
    crawlerInstance: Instance<CrawlerService>,
    private val config: BotConfig,
) : CommandHandler() {

    private val crawler: CrawlerService =
        crawlerInstance.get() ?: throw IllegalArgumentException("Invalid source specified in config")

    override val name = "q"

    @Inject
    private lateinit var log: Logger

    override suspend fun handle(args: List<String>, message: Message, user: User): Result<Unit> {
        val searchingMessage = message.reply { content = "Searching... ${Emojis.mag}" }

        log.debug("Crawling quote for '${user.username}'")

        val quote = crawlQuote()

        searchingMessage.delete()
        message.replyQuote(quote)

        return Result.success()
    }

    private suspend fun crawlQuote() = crawler crawlValidQuote config.charLimit()
}
