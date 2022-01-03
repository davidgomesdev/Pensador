package me.l3n.bot.discord.pensador.scheduler

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.service.DiscordService
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.isValid
import me.l3n.bot.discord.pensador.util.coRetry
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance


@ApplicationScoped
class PensadorScheduler(
    private val discord: DiscordService,
    crawlerInstance: Instance<CrawlerService>,
    private val log: Logger,
) {

    private val crawler: CrawlerService =
        crawlerInstance.get() ?: throw IllegalArgumentException("Invalid source specified in config")

    @Scheduled(cron = "{cron-expr}", concurrentExecution = SKIP)
    fun sendRandomQuote() = runBlocking {
        val quote = async {
            coRetry(
                5,
                block = {
                    log.debug("Crawling a quote")

                    val result = crawler.crawlRandomQuote()
                    log.info("Crawled a random quote")

                    if (result.isValid()) Result.success(result)
                    else {
                        Result.failure(IllegalArgumentException("Quote not valid"))
                    }
                },
                beforeRetry = { i -> log.debug("Retrying crawling a valid quote (#$i)") },
                afterRetry = { error -> log.debug(error.message) },
                retryExceeded = {
                    log.warn("Retry for crawling a valid quote exceeded")
                },
            ).getOrNull()
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
