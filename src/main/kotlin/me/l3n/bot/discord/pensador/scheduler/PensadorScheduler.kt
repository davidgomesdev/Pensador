package me.l3n.bot.discord.pensador.scheduler

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.service.DiscordService
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.isValid
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class PensadorScheduler(
    private val discord: DiscordService,
    private val crawler: CrawlerService,
    private val log: Logger,
) {

    @Scheduled(cron = "{cron-expr}", concurrentExecution = SKIP)
    fun sendRandomQuote() = runBlocking {
        val quote = async {
            for (i in 0..5) {
                if (i != 0)
                    log.debug("Retrying crawling a valid quote (#$i)")

                log.debug("Crawling a quote")

                val result = crawler.crawlRandomQuote()
                log.info("Crawled a random quote")

                if (result.isValid()) return@async result

                log.debug("Quote not valid")

                continue
            }

            log.warn("Retry for crawling a valid quote exceeded")
            return@async null
        }

        val cleanupJob = launch {
            discord.cleanupFreshQuotes()
            log.info("Cleaned up fresh quotes channel")
        }
        cleanupJob.join()

        discord.sendQuote(quote.await() ?: return@runBlocking)
        log.info("Sent a fresh quote")
    }
}
