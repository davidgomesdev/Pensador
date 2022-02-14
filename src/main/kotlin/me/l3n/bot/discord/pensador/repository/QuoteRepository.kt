package me.l3n.bot.discord.pensador.repository

import me.l3n.bot.discord.pensador.model.MongoQuote
import me.l3n.bot.discord.pensador.model.Quote
import me.l3n.bot.discord.pensador.service.crawler.CrawledQuote
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance

interface QuoteRepository {
    suspend fun getLast(): Quote?

    suspend fun isQuoteNew(crawled: CrawledQuote): Boolean

    suspend fun save(crawled: CrawledQuote)
}

@ApplicationScoped
class QuoteRepositoryImpl(
    collectionInstance: Instance<CoroutineCollection<MongoQuote>>,
) : QuoteRepository {

    private val collection: CoroutineCollection<MongoQuote> = collectionInstance.get()

    // The first is the current one, thus the skip
    override suspend fun getLast(): Quote? =
        collection.find().sort(
            descending(MongoQuote::_id)
        ).limit(1).skip(1).first()?.quote

    override suspend fun isQuoteNew(crawled: CrawledQuote): Boolean =
        collection.findOne(MongoQuote::id eq crawled.id) == null

    override suspend fun save(crawled: CrawledQuote) {
        collection.insertOne(MongoQuote(null, crawled.id, crawled.quote))
    }
}
