package cz.ubuntu.archiver

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.http.client.utils.URIBuilder
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object App {

    private val log = LoggerFactory.getLogger(App::class.java)!!

    @JvmStatic
    fun main(args: Array<String>) {
        check(args.isNotEmpty()) { "At least one start URL expected as an argument." }
        runBlocking {
            args.map(::runAsync).forEach { it.join() }
        }
    }

    private fun runAsync(startUrl: String): Job = GlobalScope.launch {
        try {
            run(startUrl)
            log.info("$startUrl crawled and results saved.")
        } catch (e: java.nio.file.FileAlreadyExistsException) {
            log.error("File already exists: ${e.message}")
        } catch (t: Throwable) {
            log.error("Error crawling $startUrl.", t)
        }
    }

    private fun run(startUrl: String) {
        val outputFile = Paths.get(".", "out")
                .let { Files.createDirectories(it) }
                .let { Paths.get(it.toString(), "${URIBuilder(startUrl).host}_output.txt") }
                .let { Files.createFile(it) }
                .let(Path::toFile)
        check(outputFile.canWrite()) { "The output file is not writable." }

        val output = Crawler().crawl(startUrl).joinToString(separator = "\n")

        outputFile.bufferedWriter(StandardCharsets.UTF_8).use {
            it.write(output)
        }
    }
}
