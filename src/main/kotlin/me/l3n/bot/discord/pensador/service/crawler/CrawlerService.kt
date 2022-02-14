package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import me.l3n.bot.discord.pensador.model.Author
import me.l3n.bot.discord.pensador.model.Quote
import me.l3n.bot.discord.pensador.repository.QuoteRepository
import me.l3n.bot.discord.pensador.service.isValid
import me.l3n.bot.discord.pensador.util.retryUntil
import org.jboss.logging.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Inject


data class CrawledQuote(val id: String, val quote: Quote)

abstract class CrawlerService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var http: HttpClient

    @Inject
    private lateinit var quoteRepo: QuoteRepository

    suspend infix fun crawlUniqueQuote(charLimit: Int) =
        retryUntil(
            block = {
                log.debug("Crawling a random quote")
                crawlRandomQuote(charLimit)
            },
            isValid = { crawled ->
                quoteRepo.isQuoteNew(crawled)
            },
            beforeRetry = { log.debug("Retrying to crawl a valid quote") },
            afterRetry = { log.debug("Quote is not new") },
        ).run {
            log.info("Crawled quote")

            log.debug("Adding to database")
            quoteRepo.save(this)
            log.debug("Added to database")

            quote
        }

    suspend infix fun crawlValidQuote(charLimit: Int): Quote = crawlRandomQuote(charLimit).quote

    private suspend fun crawlRandomQuote(charLimit: Int): CrawledQuote =
        retryUntil(
            block = { crawlPageRandomQuote(getRandomPage()) },
            isValid = { crawled -> crawled.quote.text.length <= charLimit && crawled.quote.isValid() },
            beforeRetry = { log.debug("Crawled quote too large, retrying") }
        )

    private suspend fun crawlPageRandomQuote(page: Int): CrawledQuote {
        val pageUrl = getPageUrl(page)
        val pageHtml = parseHtml(pageUrl)

        val quotesHtml = extractQuotesHtml(pageHtml)

        return parseQuote(quotesHtml.toList().random())
    }

    private suspend fun parseQuote(quoteHtml: Element): CrawledQuote {
        val id = getId(quoteHtml)
        val content = getQuoteContent(quoteHtml)
        val authorHtml = getAuthorHtml(quoteHtml)
        val authorName = getAuthorName(authorHtml).trim()
        val authorImageUrl =
            getAuthorImageUrl(authorHtml)?.takeIf { isImageUrl(it) }

        return CrawledQuote(
            id,
            Quote(
                Author(authorName, authorImageUrl),
                content,
            ),
        )
    }

    private fun getRandomPage() = (0 until getMaxPageCount()).random() + 1

    protected abstract fun getMaxPageCount(): Int

    protected abstract fun getPageUrl(page: Int): String

    protected suspend fun parseHtml(url: String): Document {
        // Can't simply `.get<String>(url)`, because otherwise we get an "Unresolved Class" exception
        // that occurs only when using KMongo... Don't know why
        val html = String(http.get<HttpResponse>(url).content.toByteArray())

        return Jsoup.parse(html)
    }

    protected abstract fun extractQuotesHtml(rootHtml: Document): Elements

    private suspend fun isImageUrl(url: String) = url.let {
        url.isNotBlank() && http.get<HttpResponse>(url).let { response ->
            response.status == HttpStatusCode.OK &&
                response.contentType()?.match(ContentType.Image.Any) ?: false
        }
    }

    protected abstract fun getQuoteContent(quoteHtml: Element): String

    protected abstract fun getId(quoteHtml: Element): String

    protected abstract fun getAuthorHtml(quoteHtml: Element): Element

    protected abstract fun getAuthorName(authorHtml: Element): String

    protected abstract fun getAuthorImageUrl(authorHtml: Element): String?
}
