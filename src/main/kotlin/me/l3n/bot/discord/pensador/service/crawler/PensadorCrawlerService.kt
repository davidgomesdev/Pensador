package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.ktor.client.request.*
import io.quarkus.arc.lookup.LookupIfProperty
import me.l3n.bot.discord.pensador.config.PensadorUrlConfig
import org.jboss.logging.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@LookupIfProperty(name = "source", stringValue = "pensador")
@ApplicationScoped
class PensadorCrawlerService(
    private val httpClient: HttpClient,
    private val urlConfig: PensadorUrlConfig,
) : CrawlerService {

    @Inject
    lateinit var log: Logger

    override suspend fun crawlRandomQuote(): Quote {
        val page = (0 until 20).random() + 1
        val url = "${urlConfig.populares()}/$page"

        val html = httpClient.get<String>(url)
        log.info("Fetched quote page")

        log.debug("Parsing $url")

        val root = Jsoup.parse(html)
        val quotes = root.getElementsByClass("thought-card")

        val randomIndex = (0 until quotes.count()).random()
        val randomQuote = quotes[randomIndex]
            ?: throw IllegalAccessError("No quote at $randomIndex of '$url'")

        val authorElement = randomQuote
            .getElementsByClass("autor").first()
            ?.getElementsByTag("a")?.first() ?: throw IllegalAccessError("No author in '$url'")

        val authorBioURL = authorElement.attr("href")
        val authorName = authorElement.text()
        val imageUrl = getAuthorImageUrl(authorBioURL)

        val text = randomQuote
            .getElementsByTag("p").first()?.text() ?: throw IllegalAccessError("No text in '$url'")

        log.info("Parsed whole quote")

        return Quote(
            Author(imageUrl, authorName),
            text,
        )
    }

    /**
     * @param bioLink is without the base URL
     */
    private suspend fun getAuthorImageUrl(bioLink: String): String? {
        val html = httpClient.get<String>("${urlConfig.base()}$bioLink")

        val root = Jsoup.parse(html)

        val topHeader = root
            .getImgFrom("top") ?: root
            .getImgFrom("resumo") ?: return null

        return topHeader.attr("src")
    }
}

private fun Document.getImgFrom(className: String): Element? =
    getElementsByClass(className).firstOrNull()
        ?.getElementsByTag("img")?.firstOrNull()
