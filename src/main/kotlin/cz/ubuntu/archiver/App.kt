package cz.ubuntu.archiver

import org.apache.http.client.utils.URIBuilder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

object App {

    @JvmStatic
    fun main(args: Array<String>) {
        check(args.isNotEmpty()) { "Start URL expected as the first argument." }

        val (startUrl, _) = args

        val outputFile = Paths.get(".", "out")
                .let { Files.createDirectories(it) }
                .let { Paths.get(it.toString(), "${URIBuilder(startUrl).host}_output.txt") }
                .let { Files.createFile(it) }
        check(outputFile.toFile().canWrite()) { "The output file is not writable." }

        val output = Crawler().crawl(startUrl).joinToString(separator = "\n")

        Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8).use {
            it.write(output)
        }
    }
}
