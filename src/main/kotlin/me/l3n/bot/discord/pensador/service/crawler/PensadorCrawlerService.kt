package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.ktor.client.request.*
import io.quarkus.arc.properties.IfBuildProperty
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jsoup.Jsoup
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@IfBuildProperty(name = "source", stringValue = "pensador")
class PensadorCrawlerService(private val httpClient: HttpClient) : CrawlerService {

    @ConfigProperty(name = "quotes-url.pensador")
    private lateinit var quotesUrl: String

    override suspend fun crawlRandomQuote(): Quote {
        return Quote(
            Author(null, "David"),
            "yooo",
        )
    }
}