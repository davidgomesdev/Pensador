package me.l3n.bot.discord.pensador.service.crawler

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import me.l3n.bot.discord.pensador.service.isValid
import me.l3n.bot.discord.pensador.util.retry
import me.l3n.bot.discord.pensador.util.retryUntil
import org.jboss.logging.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Inject


abstract class CrawlerService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var http: HttpClient

    suspend fun crawlDiscordValidQuote(charLimit: Int) =
        retry(
            5,
            block = {
                log.debug("Crawling a quote")

                val result = crawlRandomQuote(charLimit)
                log.info("Crawled a random quote")

                if (result.isValid()) Result.success(result)
                else {
                    Result.failure(IllegalArgumentException("Quote not valid for discord"))
                }
            },
            beforeRetry = { i -> log.debug("Retrying crawling a valid quote (#$i)") },
            afterRetry = { error -> log.debug(error.message) },
            retryExceeded = { times ->
                log.warn("Retry for crawling a valid quote exceeded ($times)")
            },
        ).getOrNull()

    suspend fun crawlRandomQuote(charLimit: Int): Quote =
        retryUntil(
            block = { crawlRandomQuote() },
            isValid = { quote -> quote.text.length > charLimit },
            beforeRetry = { log.debug("Crawled quote too large, retrying") }
        )

    suspend fun crawlRandomQuote(): Quote =
        crawlPageRandomQuote(getRandomPage())

    private suspend infix fun crawlPageRandomQuote(page: Int): Quote {
        val pageUrl = getPageUrl(page)
        val pageHtml = parseHtml(pageUrl)

        val quotesHtml = extractQuotesHtml(pageHtml)

        return parseQuote(quotesHtml.toList().random())
    }

    private suspend infix fun parseQuote(quoteHtml: Element): Quote {
        val content = getQuoteContent(quoteHtml)
        val authorHtml = getAuthorHtml(quoteHtml)
        val authorName = getAuthorName(authorHtml).trim()
        val authorImageUrl =
            getAuthorImageUrl(authorHtml)?.takeIf { isImageUrl(it) }

        return Quote(
            Author(authorName, authorImageUrl),
            content,
        )
    }

    private fun getRandomPage() = (0 until getMaxPageCount()).random() + 1

    protected abstract fun getMaxPageCount(): Int

    abstract infix fun getPageUrl(page: Int): String

    protected suspend infix fun parseHtml(url: String): Document {
        val html = http.get<String>(url)

        return Jsoup.parse(html)
    }

    protected abstract infix fun extractQuotesHtml(rootHtml: Document): Elements

    private suspend infix fun isImageUrl(url: String) = url.let {
        url.isNotBlank() && http.get<HttpResponse>(url).let { response ->
            response.status == HttpStatusCode.OK &&
                response.contentType()?.match(ContentType.Image.Any) ?: false
        }
    }

    protected abstract infix fun getQuoteContent(quoteHtml: Element): String

    protected abstract infix fun getAuthorHtml(quoteHtml: Element): Element

    protected abstract infix fun getAuthorName(authorHtml: Element): String

    protected abstract infix fun getAuthorImageUrl(authorHtml: Element): String?
}
