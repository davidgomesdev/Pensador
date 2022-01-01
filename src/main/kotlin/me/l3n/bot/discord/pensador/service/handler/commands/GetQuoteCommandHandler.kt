package me.l3n.bot.discord.pensador.service.handler.commands

import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.crawler.Quote
import me.l3n.bot.discord.pensador.service.handler.CommandHandler
import me.l3n.bot.discord.pensador.service.isValid
import me.l3n.bot.discord.pensador.util.success
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class GetQuoteCommandHandler(private val crawler: CrawlerService) : CommandHandler() {

    override val name = "q"

    @Inject
    private lateinit var log: Logger

    override suspend fun handle(args: List<String>, message: Message): Result<Unit> {
        val author = message.author ?: return Result.failure(IllegalArgumentException("No author!"))

        log.debug("Crawling quote for '${author.username}'")

        val quote: Quote? = run {
            for (i in 0..5) {
                if (i != 0)
                    log.debug("Retrying crawling a valid quote (#$i)")

                log.debug("Crawling a quote")

                val result = crawler.crawlRandomQuote()
                log.info("Crawled a random quote")

                if (result.isValid()) return@run result

                log.debug("Quote not valid")

                continue
            }

            log.warn("Retry for crawling a valid quote exceeded")
            null
        }

        message.reply {
            if (quote == null)
                content = "Couldn't find a valid quote! :weary:"
            else {
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

        return Result.success()
    }
}