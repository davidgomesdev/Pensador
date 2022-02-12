package me.l3n.bot.discord.pensador.service.handler.commands

import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.x.emoji.Emojis
import me.l3n.bot.discord.pensador.config.BotConfig
import me.l3n.bot.discord.pensador.model.Quote
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.handler.CommandHandler
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

    override suspend fun handle(args: List<String>, message: Message): Result<Unit> {
        val author = message.author ?: return Result.failure(IllegalArgumentException("No author!"))
        val searchingMessage = message.reply { content = "Searching... ${Emojis.mag}" }

        log.debug("Crawling quote for '${author.username}'")

        val quote = crawlQuote()

        searchingMessage.delete()

        replyQuote(message, quote)

        return Result.success()
    }

    private suspend fun crawlQuote() = crawler crawlValidQuote config.charLimit()

    private suspend fun replyQuote(message: Message, quote: Quote) = message.reply {
        val quoteAuthor = quote.author

        this.embed {
            description = quote.text

            thumbnail {
                this.url = quoteAuthor.imageUrl ?: ""
            }

            footer {
                this.text = quote.author.name
            }
        }
    }
}