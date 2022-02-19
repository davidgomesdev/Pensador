package me.l3n.bot.discord.pensador.scheduler

import dev.kord.common.annotation.KordPreview
import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.config.BotConfig
import me.l3n.bot.discord.pensador.service.DiscordService
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance


@ApplicationScoped
class PensadorScheduler(
    private val discord: DiscordService,
    crawlerInstance: Instance<CrawlerService>,
    private val log: Logger,
    private val config: BotConfig,
) {

    private val crawler: CrawlerService =
        crawlerInstance.get() ?: throw IllegalArgumentException("Invalid source specified in config")

    @KordPreview
    @Scheduled(cron = "{cron-expr}", concurrentExecution = SKIP)
    fun sendRandomQuote() = runBlocking {
        val quote = async { crawler crawlUniqueQuote config.charLimit() }

        val cleanupJob = launch {
            discord.cleanupFreshQuotes()
            log.info("Cleaned up fresh quotes channel")
        }
        cleanupJob.join()

        discord.sendChannelQuote(quote.await())
        log.info("Sent a fresh quote")
    }
}
