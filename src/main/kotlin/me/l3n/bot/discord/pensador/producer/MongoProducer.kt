package me.l3n.bot.discord.pensador.producer

import me.l3n.bot.discord.pensador.config.MongoConfig
import me.l3n.bot.discord.pensador.model.GoodReadsQuote
import me.l3n.bot.discord.pensador.model.PensadorQuote
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import javax.enterprise.context.ApplicationScoped
import javax.inject.Singleton


@ApplicationScoped
class MongoProducer(
    val config: MongoConfig
) {

    @Singleton
    fun database() = KMongo.createClient(config.connectionString()).coroutine
        .getDatabase(config.database())
}

@ApplicationScoped
class CollectionProducer(
    private val database: CoroutineDatabase,
) {

    @Singleton
    fun pensadorCollection() =
        database.getCollection<PensadorQuote>("pensadorQuotes")

    @Singleton
    fun goodreadsCollection() =
        database.getCollection<GoodReadsQuote>("goodreadsQuotes")
}
