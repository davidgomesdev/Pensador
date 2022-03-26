package me.l3n.bot.discord.pensador.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.l3n.bot.discord.pensador.model.MongoQuote
import me.l3n.bot.discord.pensador.model.Quote
import me.l3n.bot.discord.pensador.service.crawler.CrawledQuote
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance

interface QuoteRepository {
    suspend fun getLast(): Quote?

    suspend fun exists(crawled: CrawledQuote): Boolean

    suspend fun save(crawled: CrawledQuote)

    suspend fun getFavorites(userId: Long): Flow<Quote>

    suspend fun favoriteLast(userId: Long)

    suspend fun unfavoriteLast(userId: Long)
}

@ApplicationScoped
class QuoteRepositoryImpl(
    collectionInstance: Instance<CoroutineCollection<MongoQuote>>,
) : QuoteRepository {

    private val collection: CoroutineCollection<MongoQuote> = collectionInstance.get()

    override suspend fun getLast(): Quote? =
        getLastDocument()?.quote

    override suspend fun exists(crawled: CrawledQuote): Boolean =
        collection.findOne(MongoQuote::id eq crawled.id) != null

    override suspend fun save(crawled: CrawledQuote) {
        collection.insertOne(MongoQuote(null, crawled.id, crawled.quote))
    }

    override suspend fun favoriteLast(userId: Long) {
        val quote = getCurrentDocument() ?: throw IllegalStateException("There are no quotes")

        if (!quote.favoriteUserIds.contains(userId)) {
            quote.favoriteUserIds.add(userId)
            collection.updateOne(quote)
        }
    }

    override suspend fun getFavorites(userId: Long): Flow<Quote> {
        val favorites = collection.find(
            MongoQuote::favoriteUserIds contains userId
        ).sort(
            descending(MongoQuote::_id)
        )

        return favorites.toFlow().map { it.quote }
    }

    override suspend fun unfavoriteLast(userId: Long) {
        val quote = getCurrentDocument() ?: throw IllegalStateException("There are no quotes")

        if (quote.favoriteUserIds.contains(userId)) {
            quote.favoriteUserIds.remove(userId)
            collection.updateOne(quote)
        }
    }

    // The first is the current one, thus the skip
    private suspend fun getCurrentDocument() = collection.find().sort(
        descending(MongoQuote::_id)
    ).first()

    // The first is the current one, thus the skip
    private suspend fun getLastDocument() = collection.find().sort(
        descending(MongoQuote::_id)
    ).skip(1).first()
}
