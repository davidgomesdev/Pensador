package me.l3n.bot.discord.pensador.service.crawler

data class Quote(
    val author: Author,
    val text: String,
)

data class Author(
    val name: String,
    val imageUrl: String?,
)
