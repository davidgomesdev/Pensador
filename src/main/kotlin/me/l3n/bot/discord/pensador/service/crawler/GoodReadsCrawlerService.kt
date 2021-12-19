package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.ktor.client.request.*
import io.quarkus.arc.properties.IfBuildProperty
import me.l3n.bot.discord.pensador.config.HttpConfig
import org.jsoup.Jsoup
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@IfBuildProperty(name = "source", stringValue = "goodreads", enableIfMissing = true)
class GoodReadsCrawlerService(private val config: HttpConfig, private val httpClient: HttpClient) : CrawlerService {

    private val extractQuoteRegex = Regex("(?<=“)(.*?)(?=”)")

    override suspend fun crawlRandomQuote(): Quote {
        val page = (0 until 20).random() + 1
        val html = httpClient.get<String>("${config.quotesUrl()}?page=$page")

        val rootElement = Jsoup.parse(html)
        val quotes = rootElement.getElementsByClass("quoteDetails")

        val randomIndex = (0 until quotes.count()).random()
        val randomQuote = quotes[randomIndex]

        val imageUrl = randomQuote.getElementsByTag("img")?.attr("src")
        val text = randomQuote.getElementsByClass("quoteText").text()
        val author = extractNameOnly(
            randomQuote.getElementsByClass("authorOrTitle").first().text()
        )

        return Quote(
            Author(imageUrl, author),
            extractQuote(text),
        )
    }

    private fun extractQuote(text: String) = extractQuoteRegex.find(text)?.value ?: ""

    /**
     * @return [text] with only the name (no commas for instance)
     */
    private fun extractNameOnly(text: String) = AUTHOR_NAME_REGEX.find(text)?.value?.trim() ?: ""
}
