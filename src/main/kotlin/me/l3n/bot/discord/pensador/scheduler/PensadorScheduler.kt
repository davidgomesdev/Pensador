package me.l3n.bot.discord.pensador.scheduler

import dev.kord.common.annotation.KordPreview
import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.config.BotConfig
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.discord.ChannelMessageType
import me.l3n.bot.discord.pensador.service.discord.DiscordService
import org.jboss.logging.Logger
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance


@ApplicationScoped
class PensadorScheduler(
    crawlerInstance: Instance<CrawlerService>,
    private val log: Logger,
    private val botConfig: BotConfig,
    private val discord: DiscordService,
    private val messageType: ChannelMessageType
) {

    private val crawler: CrawlerService =
        crawlerInstance.get() ?: throw IllegalArgumentException("Invalid source specified in config")

    @KordPreview
    @Scheduled(cron = "{cron-expr}", concurrentExecution = SKIP)
    fun sendRandomQuote() = runBlocking {
        val quote = async { crawler crawlUniqueQuote botConfig.charLimit() }

        val cleanupJob = launch {
            discord.cleanupFreshQuotes()
            log.info("Cleaned up fresh quotes channel")
        }
        cleanupJob.join()

        discord.sendChannelQuote(messageType, quote.await())
        log.info("Sent a fresh quote")
    }
}
