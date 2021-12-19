package me.l3n.bot.discord.pensador.scheduler

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.service.DiscordService
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class PensadorScheduler(private val discord: DiscordService, private val crawler: CrawlerService) {

    @Inject
    lateinit var log: Logger

    @Scheduled(cron = "{cron-expr}", concurrentExecution = SKIP)
    fun crawl() = runBlocking {
        val quote = async {
            log.debug("Crawling a quote")

            val result = crawler.crawlRandomQuote()
            log.info("Crawled a random quote")

            result
        }

        val cleanupJob = launch {
            discord.cleanupFreshQuotes()
            log.info("Cleaned up fresh quotes channel")
        }
        cleanupJob.join()

        discord.sendQuote(quote.await())
        log.info("Sent a fresh quote")
    }
}
