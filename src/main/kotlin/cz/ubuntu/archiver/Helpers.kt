package cz.ubuntu.archiver

import org.apache.http.client.utils.URIBuilder
import java.net.URL

class Helpers {

    companion object {

        fun isLinkToDomain(domain: String): (String) -> Boolean = {
            URL(it).host == domain
        }

        fun removeFragment(url: String): String = URIBuilder(url).setFragment(null).toString()
    }
}