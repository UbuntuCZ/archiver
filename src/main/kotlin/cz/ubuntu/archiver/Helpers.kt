package cz.ubuntu.archiver

import org.apache.http.client.utils.URIBuilder
import java.net.URL

object Helpers {

    fun isLinkToDomain(domain: String): (String) -> Boolean = {
        URL(it).host == domain
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