package me.l3n.bot.discord.pensador.scheduler

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.quarkus.test.junit.QuarkusMock
import io.quarkus.test.junit.QuarkusTest
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.service.DiscordService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject


@QuarkusTest
class PensadorSchedulerTest {

    private val service: DiscordService = mockk()

    @Inject
    lateinit var scheduler: PensadorScheduler

    @BeforeEach
    fun setup() {
        QuarkusMock.installMockForType(service, DiscordService::class.java)
    }

    @Test
    fun `logs in at start`() {
        coEvery { service.sendMessageAs(any()) }.returns(Unit)

        runBlocking { scheduler.crawl() }

        coVerify(exactly = 1) { service.sendMessageAs(any()) }
    }
}
