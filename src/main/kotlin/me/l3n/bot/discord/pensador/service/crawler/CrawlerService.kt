package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Inject

abstract class CrawlerService {

    @Inject
    private lateinit var http: HttpClient

    suspend fun crawlRandomQuote(): Quote {
        val page = getRandomPage()
        val pageUrl = getPageUrl(page)
        val pageHtml = parseHtml(pageUrl)

        val quoteHtml = extractQuotesHtml(pageHtml)[getRandomPage()]

        return parseQuote(quoteHtml)
    }

    private fun getRandomPage() = (0 until getMaxPageCount()).random() + 1

    protected abstract fun getMaxPageCount(): Int

    // TODO: turn this into a lazy sequence! (or flow?)
    suspend fun crawlQuotes(page: Int): Flow<Quote> {
        val pageUrl = getPageUrl(page)
        val pageHtml = parseHtml(pageUrl)

        val quotesHtml = extractQuotesHtml(pageHtml)

        return quotesHtml.asFlow()
            .map(::parseQuote)
    }

    abstract fun getPageUrl(page: Int): String

    protected suspend fun parseHtml(url: String): Document {
        val html = http.get<String>(url)

        return Jsoup.parse(html)
    }

    protected abstract fun extractQuotesHtml(rootHtml: Document): Elements

    private fun parseQuote(quoteHtml: Element): Quote {
        val content = getQuoteContent(quoteHtml)
        val authorHtml = getAuthorHtml(quoteHtml)
        val authorName = getAuthorName(authorHtml)
        val authorImageUrl = getAuthorImageUrl(authorHtml)

        return Quote(Author(authorName, authorImageUrl), content)
    }

    protected abstract fun getQuoteContent(quoteHtml: Element): String

    protected abstract fun getAuthorHtml(quoteHtml: Element): Element

    protected abstract fun getAuthorName(authorHtml: Element): String

    protected abstract fun getAuthorImageUrl(authorHtml: Element): String?
}
