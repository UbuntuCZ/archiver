package cz.ubuntu.archiver

import org.apache.http.HttpStatus
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import org.slf4j.LoggerFactory
import java.net.URISyntaxException
import java.net.URL
import java.time.Duration
import java.time.Instant

class Crawler {

    fun crawl(startUrl: String): Set<String> {
        val start = Instant.now()
        val domain = URL(startUrl).host

        val toCrawl = UniqueQueue(listOf(startUrl))
        val crawled = HashSet<String>()
        val ignored = HashSet<String>()

        while (toCrawl.hasNext()) {
            val url = toCrawl.poll()!!

            if (ignored.contains(url) || crawled.contains(url)) {
                continue
            }

            // Load url and add all links for future crawling
            try {
                Jsoup.connect(url).get()
                        .let {
                            listOf(
                                    it.select("a[href]"),
                                    it.select("link[rel=canonical]"),
                                    it.select("link[rel=shortlink]"),
                                    it.select("link[rel=alternate]:not([type])")
                            )
                        }
                        .flatten()
                        .map { it.absUrl("href") }
                        .distinct()
                        .filterNot(String::isNullOrEmpty)
                        .map(Helpers::encodeUrl)
                        .filter(Helpers.isLinkToDomain(domain))
                        .filterNot(Helpers::isUrlToSkip)
                        .map(Helpers::removeFragment)
                        .map(Helpers.removeQueryStringKey(*queryStringKeysToRemove))
                        .filterNot(url::equals)
                        .filterNot(ignored::contains)
                        .filterNot(crawled::contains)
                        .forEach(toCrawl::offer)
                crawled.add(url)
            } catch (_: UnsupportedMimeTypeException) {
                crawled.add(url)
            } catch (e: HttpStatusException) {
                when (e.statusCode) {
                    HttpStatus.SC_FORBIDDEN or HttpStatus.SC_NOT_FOUND -> ignored.add(url)
                }
            } catch (e: URISyntaxException) {
                log.warn("Invalid URL $url: ${e.message}")
                ignored.add(url)
            } catch (t: Throwable) {
                log.warn("Could not crawl $url.", t)
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
