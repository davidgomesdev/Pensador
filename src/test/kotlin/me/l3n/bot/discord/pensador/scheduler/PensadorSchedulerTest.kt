package me.l3n.bot.discord.pensador.scheduler

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.service.DiscordService
import me.l3n.bot.discord.pensador.service.crawler.Author
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.crawler.Quote
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.enterprise.inject.Instance

class PensadorSchedulerTest {

    private val dummyAuthor = Author("Fernando Person", "http://image.l3n/fernando_person.png")
    private val dummyQuote = Quote(dummyAuthor, "O meu passado é tudo quanto não consegui ser.")

    private val serviceMock: DiscordService = mockk()
    private val crawlerMock: CrawlerService = mockk()
    private val crawlerInstanceMock: Instance<CrawlerService> =
        mockk { every { get() } returns crawlerMock }

    private val scheduler = PensadorScheduler(serviceMock, crawlerInstanceMock, mockk(relaxUnitFun = true))

    @BeforeEach
    fun setupMocks() {
        coEvery { crawlerMock.crawlRandomQuote() }.returns(dummyQuote)
        coEvery { serviceMock.sendQuote(any()) }.returns(Unit)
        coEvery { serviceMock.cleanupFreshQuotes() }.returns(Unit)
    }

    @Test
    fun `should crawl a quote and send it to Discord`() {
        runBlocking { scheduler.sendRandomQuote() }

        coVerify(exactly = 1) { crawlerMock.crawlRandomQuote() }
        coVerify(exactly = 1) { serviceMock.sendQuote(dummyQuote) }
    }
}
