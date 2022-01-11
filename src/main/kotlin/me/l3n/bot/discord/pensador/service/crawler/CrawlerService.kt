package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import me.l3n.bot.discord.pensador.util.NoArgConstructor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Inject


@NoArgConstructor
abstract class CrawlerService {

    @Inject
    private lateinit var http: HttpClient

    suspend fun crawlRandomQuote(): Quote {
        val page = getRandomPage()
        val pageUrl = getPageUrl(page)
        val pageHtml = parseHtml(pageUrl)

        val quotesHtml = extractQuotesHtml(pageHtml)

        return parseQuote(quotesHtml.random())
    }

    private fun getRandomPage() = (0 until getMaxPageCount()).random() + 1

    protected abstract fun getMaxPageCount(): Int

    suspend infix fun crawlQuotes(page: Int): Flow<Quote> {
        val pageUrl = getPageUrl(page)
        val pageHtml = parseHtml(pageUrl)

        val quotesHtml = extractQuotesHtml(pageHtml)

        return quotesHtml.asFlow()
            .map(::parseQuote)
    }

    abstract infix fun getPageUrl(page: Int): String

    protected suspend infix fun parseHtml(url: String): Document {
        val html = http.get<String>(url)

        return Jsoup.parse(html)
    }

    protected abstract infix fun extractQuotesHtml(rootHtml: Document): Elements

    private infix fun parseQuote(quoteHtml: Element): Quote {
        val content = getQuoteContent(quoteHtml)
        val authorHtml = getAuthorHtml(quoteHtml)
        val authorName = getAuthorName(authorHtml).trim()
        val authorImageUrl = getAuthorImageUrl(authorHtml)

        return Quote(Author(authorName, authorImageUrl), content)
    }

    protected abstract infix fun getQuoteContent(quoteHtml: Element): String

    protected abstract infix fun getAuthorHtml(quoteHtml: Element): Element

    protected abstract infix fun getAuthorName(authorHtml: Element): String

    protected abstract infix fun getAuthorImageUrl(authorHtml: Element): String?
}
