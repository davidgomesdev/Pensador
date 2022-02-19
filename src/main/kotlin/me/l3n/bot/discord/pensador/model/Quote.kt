package me.l3n.bot.discord.pensador.model

import org.bson.types.ObjectId

data class MongoQuote(
    val _id: ObjectId?,
    val id: String,
    val quote: Quote,
    val favoriteUserIds: MutableList<Long> = mutableListOf(),
)

data class Quote(
    val author: Author,
    val text: String,
)

data class Author(
    val name: String,
    val imageUrl: String?,
)
