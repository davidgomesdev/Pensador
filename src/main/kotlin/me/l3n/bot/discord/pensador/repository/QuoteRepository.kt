package me.l3n.bot.discord.pensador.repository

import kotlinx.coroutines.flow.Flow
import me.l3n.bot.discord.pensador.model.Quote
import me.l3n.bot.discord.pensador.service.crawler.CrawledQuote

interface QuoteRepository {
    suspend fun getLast(): Quote?

    suspend fun exists(crawled: CrawledQuote): Boolean

    suspend fun save(crawled: CrawledQuote)

    suspend fun getFavorites(userId: ULong): Flow<Quote>

    suspend fun favoriteLast(userId: ULong)

    suspend fun unfavoriteLast(userId: ULong)
}
