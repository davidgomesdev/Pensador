package me.l3n.bot.discord.pensador.service

import io.ktor.client.*
import io.ktor.client.request.*
import me.l3n.bot.discord.pensador.config.HttpConfig
import org.jsoup.Jsoup
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CrawlerService(private val config: HttpConfig, private val httpClient: HttpClient) {

    suspend fun crawlRandomQuote(): Quote {
        val html = httpClient.get<String>(config.quotesUrl())
        val rootElement = Jsoup.parse(html)
        val quotes = rootElement.getElementsByClass("quoteDetails")

        val randomIndex = (0 until quotes.count()).random()
        val randomQuote = quotes[randomIndex]

        val imageUrl = randomQuote.getElementsByTag("img")?.attr("src")
        val text = randomQuote.getElementsByClass("quoteText").text()
            .trim()
        val author = randomQuote.getElementsByClass("authorOrTitle").first().text()

        return Quote(
            Author(imageUrl, author),
            text,
        )
    }
}

data class Quote(
    val author: Author,
    val text: String,
)

data class Author(
    val imageUrl: String?,
    val name: String,
)
