package me.l3n.bot.discord.pensador.service.crawler

data class Quote(
    val author: Author,
    val text: String,
)

data class Author(
    val imageUrl: String?,
    val name: String,
)

val AUTHOR_NAME_REGEX = "^[A-Za-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ' ]+\$".toRegex()
