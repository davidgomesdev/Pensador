package me.l3n.bot.discord.pensador.repository

import io.quarkus.arc.properties.IfBuildProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.l3n.bot.discord.pensador.model.Quote
import me.l3n.bot.discord.pensador.service.crawler.CrawledQuote
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
@IfBuildProperty(name = "disabled.persistence", stringValue = "true")
class NullQuoteRepository : QuoteRepository {
    override suspend fun getLast(): Quote? = null

    override suspend fun exists(crawled: CrawledQuote): Boolean = false

    override suspend fun save(crawled: CrawledQuote) {}

    override suspend fun getFavorites(userId: ULong): Flow<Quote> = flowOf()

    override suspend fun favoriteLast(userId: ULong) {}

    override suspend fun unfavoriteLast(userId: ULong) {}
}