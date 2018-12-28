package cz.ubuntu.archiver

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URL
import java.time.Duration
import java.time.Instant

class Crawler {

    fun crawl(startUrl: String): Set<String> {
        val start = Instant.now()
        val domain = URL(startUrl).host

        val toCrawl = UniqueQueue(listOf(startUrl))
        val crawled = HashSet<String>()

        while (toCrawl.hasNext()) {
            val url = toCrawl.poll()

            // Skip this url if crawled already
            if (url == null || !crawled.add(url)) {
                continue
            }

            // Load url and add all links for future crawling
            try {
                Jsoup.connect(url).get()
                        .let { listOf(
                                it.select("a[href]"),
                                it.select("link[rel=canonical]"),
                                it.select("link[rel=alternate]")
                        ) }
                        .flatten()
                        .map { it.absUrl("href") }
                        .filter(Helpers.isLinkToDomain(domain))
                        .map { Helpers.removeFragment(it) }
                        .map(Helpers.removeQueryStringKey(*queryStringKeysToRemove))
                        .filterNot(crawled::contains)
                        .forEach { toCrawl.offer(it) }
            } catch (e: IOException) {
                log.warn("Could not crawl $url.", e)
            }

            if (crawled.size % 100 == 0) {
                log.info("Crawled ${crawled.size} urls from $domain in ${Duration.between(start, Instant.now()).seconds} seconds, ${toCrawl.size()} enqueued to be crawled.")
            }
        }

        return crawled
    }

    companion object {
        private val log = LoggerFactory.getLogger(Crawler::class.java)!!
        private val queryStringKeysToRemove = arrayOf("PHPSESSID", "do")
    }
}
