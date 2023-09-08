package me.l3n.bot.discord.pensador.producer

import io.quarkus.arc.lookup.LookupIfProperty
import me.l3n.bot.discord.pensador.config.MongoConfig
import me.l3n.bot.discord.pensador.model.MongoQuote
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Singleton


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

    @LookupIfProperty(name = "source", stringValue = "pensador")
    @Singleton
    fun pensadorCollection() =
        database.getCollection<MongoQuote>("pensadorQuotes")

    @LookupIfProperty(name = "source", stringValue = "goodreads", lookupIfMissing = true)
    @Singleton
    fun goodreadsCollection() =
        database.getCollection<MongoQuote>("goodreadsQuotes")
}
