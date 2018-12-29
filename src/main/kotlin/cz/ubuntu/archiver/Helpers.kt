package cz.ubuntu.archiver

import org.apache.http.client.utils.URIBuilder
import java.net.URL

object Helpers {

    fun encodeUrl(url: String): String = url.replace(" ", "%20")

    fun isLinkToDomain(domain: String): (String) -> Boolean = {
        URL(it).host == domain
    }

    fun isUrlToSkip(url: String): Boolean =
            URL(url).let { skipOnPath(it) || skipOnQueryString(it) }

    private fun skipOnPath(urlObj: URL) =
            if (urlObj.path != null) {
                val path = urlObj.path.toLowerCase()
                listOf(
                        Regex("^/user/login/?"),
                        Regex("^/user/register/?"),
                        Regex("^/_export/.*"),
                        Regex("^/_detail/.*")
                ).any(path::matches)
            } else {
                false
            }

    private fun skipOnQueryString(urlObj: URL) =
            if (urlObj.query != null) {
                val query = urlObj.query.toLowerCase()
                listOf(
                        Regex("(^|.*&)do=.*"),
                        Regex("(^|.*&)action=profile;u=.*")
                ).any(query::matches)
            } else {
                false
            }

    fun removeFragment(url: String): String = URIBuilder(url).setFragment(null).toString()

    fun removeQueryStringKey(vararg key: String): (String) -> String = { url ->
        val builder = URIBuilder(url)
        builder.setParameters(
                builder.queryParams.filterNot { key.contains(it.name) }
        )
        builder.toString()
    }
}