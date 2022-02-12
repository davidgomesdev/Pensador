package me.l3n.bot.discord.pensador.service.crawler

import io.quarkus.arc.lookup.LookupIfProperty
import me.l3n.bot.discord.pensador.config.GoodReadsConfig
import me.l3n.bot.discord.pensador.config.GoodReadsUrlConfig
import me.l3n.bot.discord.pensador.model.GoodReadsQuote
import me.l3n.bot.discord.pensador.util.toPlainText
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import javax.enterprise.context.ApplicationScoped


private val EXTRACT_QUOTE_REGEX = "(?<=“)(.*?)(?=”)".toRegex(RegexOption.DOT_MATCHES_ALL)
private val AUTHOR_NAME_REPLACE_REGEX = "[^A-Za-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ' .,]+|,\$".toRegex()

@LookupIfProperty(name = "source", stringValue = "goodreads", lookupIfMissing = true)
@ApplicationScoped
class GoodReadsCrawlerService(
    private val urlConfig: GoodReadsUrlConfig,
    private val config: GoodReadsConfig,
    private val collection: CoroutineCollection<GoodReadsQuote>,
) : CrawlerService() {

    override fun getMaxPageCount(): Int = config.pageCount()

    override infix fun getPageUrl(page: Int): String = "${urlConfig.quotes()}?page=$page"

    override infix fun extractQuotesHtml(rootHtml: Document): Elements =
        rootHtml.getElementsByClass("quoteDetails")

    override infix fun getQuoteContent(quoteHtml: Element): String =
        extractQuote(
            quoteHtml.getElementsByClass("quoteText").first()?.toPlainText()
                ?: throw IllegalArgumentException("No quote text")
        )

    override fun getId(quoteHtml: Element): String =
        quoteHtml
            .select(".quoteFooter .right a.smallText").first()
            ?.attr("href")
            ?: throw IllegalArgumentException("No ID")

    override infix fun getAuthorHtml(quoteHtml: Element): Element =
        quoteHtml

    override infix fun getAuthorName(authorHtml: Element): String {
        val nameHtml = authorHtml.select("span.authorOrTitle").first() ?: return ""

        return extractNameOnly(nameHtml.wholeText().trim())
    }

    override infix fun getAuthorImageUrl(authorHtml: Element): String? =
        authorHtml.getElementsByTag("img")
            .attr("src")

    override suspend fun isQuoteNew(crawled: CrawledQuote): Boolean =
        collection.findOne(GoodReadsQuote::id eq crawled.id) == null

    override suspend fun persistToDB(crawled: CrawledQuote) {
        collection.insertOne(GoodReadsQuote(crawled.id, crawled.quote))
    }

    private infix fun extractQuote(text: String) = EXTRACT_QUOTE_REGEX.find(text)?.value ?: ""

    /**
     * @return [text] with only the name (no commas for instance)
     */
    private fun extractNameOnly(text: String) = AUTHOR_NAME_REPLACE_REGEX.replace(text, "")
}
