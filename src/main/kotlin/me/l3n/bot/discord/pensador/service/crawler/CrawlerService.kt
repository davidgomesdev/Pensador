package me.l3n.bot.discord.pensador.service.crawler

interface CrawlerService {

    suspend fun crawlRandomQuote(): Quote
}
