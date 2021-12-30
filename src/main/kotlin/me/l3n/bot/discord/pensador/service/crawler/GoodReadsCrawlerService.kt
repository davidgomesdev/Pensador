package me.l3n.bot.discord.pensador.service.crawler

import io.quarkus.arc.lookup.LookupIfProperty
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.enterprise.context.ApplicationScoped


private val EXTRACT_QUOTE_REGEX = "(?<=“)(.*?)(?=”)".toRegex()
private val AUTHOR_NAME_REGEX = "^[A-Za-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ' ]+\$".toRegex()

@LookupIfProperty(name = "source", stringValue = "goodreads", lookupIfMissing = true)
@ApplicationScoped
class GoodReadsCrawlerService : CrawlerService() {

    @ConfigProperty(name = "url.goodreads-quotes")
    private lateinit var quotesUrl: String

    override fun getMaxPageCount(): Int = 20

    override fun getPageUrl(page: Int): String = "$quotesUrl?page=$page"

    override fun extractQuotesHtml(rootHtml: Document): Elements =
        rootHtml.getElementsByClass("quoteDetails")

    override fun getQuoteContent(quoteHtml: Element): String =
        extractQuote(quoteHtml.getElementsByClass("quoteText").text())

    override fun getAuthorHtml(quoteHtml: Element): Element =
        quoteHtml

    override fun getAuthorName(authorHtml: Element): String =
        extractNameOnly(
            authorHtml
                .getElementsByClass("authorOrTitle").first()?.text()
                ?: throw IllegalAccessError("No author name")
        )

    override fun getAuthorImageUrl(authorHtml: Element): String? =
        authorHtml.getElementsByTag("img")
            .attr("src")

    private fun extractQuote(text: String) = EXTRACT_QUOTE_REGEX.find(text)?.value ?: ""

    /**
     * @return [text] with only the name (no commas for instance)
     */
    private fun extractNameOnly(text: String) = AUTHOR_NAME_REGEX.find(text)?.value ?: ""
}
