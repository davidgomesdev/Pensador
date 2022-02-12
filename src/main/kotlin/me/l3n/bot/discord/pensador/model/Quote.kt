package me.l3n.bot.discord.pensador.model

data class PensadorQuote(val id: String, val quote: Quote)
data class GoodReadsQuote(val id: String, val quote: Quote)

data class Quote(
    val author: Author,
    val text: String,
)

data class Author(
    val name: String,
    val imageUrl: String?,
)
