package me.l3n.bot.discord.pensador.scheduler

import io.quarkus.scheduler.Scheduled
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.service.CrawlerService
import me.l3n.bot.discord.pensador.service.DiscordService
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class PensadorScheduler(private val discord: DiscordService, private val crawler: CrawlerService) {

    @Inject
    lateinit var log: Logger

    @Scheduled(every = "{crawler.period}")
    fun crawl() = runBlocking {
        val quote = crawler.crawlRandomQuote()

        discord.sendQuote(quote)
    }
}
