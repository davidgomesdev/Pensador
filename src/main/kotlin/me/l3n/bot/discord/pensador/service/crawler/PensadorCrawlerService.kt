package me.l3n.bot.discord.pensador.service.crawler

import io.quarkus.arc.lookup.LookupIfProperty
import kotlinx.coroutines.runBlocking
import me.l3n.bot.discord.pensador.config.PensadorUrlConfig
import me.l3n.bot.discord.pensador.util.toPlainText
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Singleton

@LookupIfProperty(name = "source", stringValue = "pensador")
@Singleton
class PensadorCrawlerService(
    private val urlConfig: PensadorUrlConfig,
) : CrawlerService() {

    override fun getMaxPageCount(): Int = 20

    override infix fun getPageUrl(page: Int): String = "${urlConfig.populares()}/$page"

    override infix fun extractQuotesHtml(rootHtml: Document): Elements =
        rootHtml.getElementsByClass("thought-card")

    override infix fun getQuoteContent(quoteHtml: Element): String =
        quoteHtml.getElementsByTag("p").first()?.toPlainText() ?: throw IllegalAccessError("No text")

    override infix fun getAuthorHtml(quoteHtml: Element): Element =
        quoteHtml
            .getElementsByClass("autor").first()
            ?.getElementsByTag("a")?.first() ?: throw IllegalAccessError("No author")

    override infix fun getAuthorName(authorHtml: Element): String =
        authorHtml.text()

    override infix fun getAuthorImageUrl(authorHtml: Element): String? {
        val bioLink = authorHtml.attr("href")
        val html = runBlocking { parseHtml("${urlConfig.base()}$bioLink") }

        val topHeader = html getImgFrom "top" ?: html getImgFrom "resumo" ?: return null

        return topHeader.attr("src")
    }
}

private infix fun Document.getImgFrom(className: String): Element? =
    getElementsByClass(className).firstOrNull()
        ?.getElementsByTag("img")?.firstOrNull()
