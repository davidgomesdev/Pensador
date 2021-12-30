package me.l3n.bot.discord.pensador.service.crawler

import io.quarkus.arc.DefaultBean
import io.quarkus.arc.lookup.LookupIfProperty
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.config.PensadorUrlConfig
import org.jboss.logging.Logger
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@LookupIfProperty(name = "source", stringValue = "pensador")
@DefaultBean
@ApplicationScoped
class PensadorCrawlerService(
    private val urlConfig: PensadorUrlConfig,
) : CrawlerService() {

    @Inject
    lateinit var log: Logger

    override fun getMaxPageCount(): Int = 20

    override fun getPageUrl(page: Int): String = "${urlConfig.populares()}/$page"

    override fun extractQuotesHtml(rootHtml: Document): Elements =
        rootHtml.getElementsByClass("thought-card")

    override fun getQuoteContent(quoteHtml: Element): String =
        quoteHtml.getElementsByTag("p").first()?.text() ?: throw IllegalAccessError("No text")

    override fun getAuthorHtml(quoteHtml: Element): Element =
        quoteHtml
            .getElementsByClass("autor").first()
            ?.getElementsByTag("a")?.first() ?: throw IllegalAccessError("No author")

    override fun getAuthorName(authorHtml: Element): String =
        authorHtml.text()

    override fun getAuthorImageUrl(authorHtml: Element): String? {
        val bioLink = authorHtml.attr("href")
        val html = runBlocking { parseHtml("${urlConfig.base()}$bioLink") }

        val topHeader = html
            .getImgFrom("top") ?: html
            .getImgFrom("resumo") ?: return null

        return topHeader.attr("src")
    }
}

private fun Document.getImgFrom(className: String): Element? =
    getElementsByClass(className).firstOrNull()
        ?.getElementsByTag("img")?.firstOrNull()
