package me.l3n.bot.discord.pensador.service.discord.handler.commands

import me.l3n.bot.discord.pensador.config.BotConfig
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.discord.handler.CommandContext
import me.l3n.bot.discord.pensador.service.discord.handler.CommandHandler
import me.l3n.bot.discord.pensador.service.discord.replyQuote
import me.l3n.bot.discord.pensador.util.success
import org.jboss.logging.Logger
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject


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

    override suspend fun handle(args: List<String>, context: CommandContext): Result<Unit> = context.run {
        log.debug("Crawling quote for '${context.user.username}'")

        message.replyQuote(crawlQuote())

        return Result.success()
    }

    private suspend fun crawlQuote() = crawler crawlValidQuote config.charLimit()
}
