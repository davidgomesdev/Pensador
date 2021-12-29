package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.ktor.client.request.*
import io.quarkus.arc.DefaultBean
import io.quarkus.arc.lookup.LookupIfProperty
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jsoup.Jsoup
import javax.enterprise.context.ApplicationScoped

@LookupIfProperty(name = "source", stringValue = "goodreads", lookupIfMissing = true)
@DefaultBean
@ApplicationScoped
class GoodReadsCrawlerService(private val httpClient: HttpClient) : CrawlerService {

    @ConfigProperty(name = "url.goodreads-quotes")
    private lateinit var quotesUrl: String

    private val extractQuoteRegex = Regex("(?<=“)(.*?)(?=”)")

    override suspend fun crawlRandomQuote(): Quote {
        val page = (0 until 20).random() + 1
        val url = "$quotesUrl?page=$page"
        val html = httpClient.get<String>()

        val root = Jsoup.parse(html)
        val quotes = root.getElementsByClass("quoteDetails")

        val randomIndex = (0 until quotes.count()).random()
        val randomQuote = quotes[randomIndex]
            ?: throw IllegalAccessError("No quote at $randomIndex of '$url'")

        val imageUrl = randomQuote
            .getElementsByTag("img")
            .attr("src")
        val author = extractNameOnly(
            randomQuote
                .getElementsByClass("authorOrTitle").first()?.text()
                ?: throw IllegalAccessError("No author/title in '$url'")
        )
        val text = randomQuote
            .getElementsByClass("quoteText").text()

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
