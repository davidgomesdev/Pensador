package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.quarkus.arc.properties.IfBuildProperty
import me.l3n.bot.discord.pensador.config.HttpConfig
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@IfBuildProperty(name = "source", stringValue = "pensador")
class PensadorCrawlerService(private val config: HttpConfig, private val httpClient: HttpClient) : CrawlerService {

    override suspend fun crawlRandomQuote(): Quote {
        return Quote(
            Author(null, "David"),
            "yooo",
        )
    }
}