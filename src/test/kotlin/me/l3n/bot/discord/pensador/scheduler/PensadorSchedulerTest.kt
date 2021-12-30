package me.l3n.bot.discord.pensador.scheduler

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.quarkus.test.junit.QuarkusMock
import io.quarkus.test.junit.QuarkusTest
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.service.DiscordService
import me.l3n.bot.discord.pensador.service.crawler.Author
import me.l3n.bot.discord.pensador.service.crawler.CrawlerService
import me.l3n.bot.discord.pensador.service.crawler.Quote
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
class PensadorSchedulerTest {

    private val dummyAuthor = Author("http://image.l3n/fernando_person.png", "Fernando Person")
    private val dummyQuote = Quote(dummyAuthor, "O meu passado é tudo quanto não consegui ser.")

    private val service: DiscordService = mockk()
    private val crawler: CrawlerService = mockk()

    @Inject
    private lateinit var scheduler: PensadorScheduler

    @BeforeEach
    fun setupMocks() {
        QuarkusMock.installMockForType(service, DiscordService::class.java)
        QuarkusMock.installMockForType(crawler, CrawlerService::class.java)
    }

    @Test
    fun `should crawl a quote and send it to Discord`() {
        coEvery { crawler.crawlRandomQuote() }.returns(dummyQuote)
        coEvery { service.sendQuote(any()) }.returns(Unit)

        runBlocking { scheduler.sendRandomQuote() }

        coVerify(exactly = 1) { crawler.crawlRandomQuote() }
        coVerify(exactly = 1) { service.sendQuote(dummyQuote) }
    }
}
