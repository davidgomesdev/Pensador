package me.l3n.bot.discord.pensador.scheduler

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.service.DiscordService
import me.l3n.bot.discord.pensador.service.crawler.Author
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.crawler.Quote
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PensadorSchedulerTest {

    private val dummyAuthor = Author("Fernando Person", "http://image.l3n/fernando_person.png")
    private val dummyQuote = Quote(dummyAuthor, "O meu passado é tudo quanto não consegui ser.")

    private val service: DiscordService = mockk()
    private val crawler: CrawlerService = mockk()

    private val scheduler = PensadorScheduler(service, crawler, mockk(relaxUnitFun = true))

    @BeforeEach
    fun setupMocks() {
        coEvery { crawler.crawlRandomQuote() }.returns(dummyQuote)
        coEvery { service.sendQuote(any()) }.returns(Unit)
        coEvery { service.cleanupFreshQuotes() }.returns(Unit)
    }

    @Test
    fun `should crawl a quote and send it to Discord`() {
        runBlocking { scheduler.sendRandomQuote() }

        coVerify(exactly = 1) { crawler.crawlRandomQuote() }
        coVerify(exactly = 1) { service.sendQuote(dummyQuote) }
    }
}
