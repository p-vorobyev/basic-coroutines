package dev.voroby.coroutines

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

internal class RequestTest {

    private val request = Request(GitHubService())

    private val log: Logger = LoggerFactory.getLogger(RequestTest::class.java)

    @Test
    fun loadContributorsBlocking() {
        var users: List<User>
        val millis = measureTimeMillis {
            users = request.loadContributorsBlocking("cats")
        }
        log.info("Contributors size: ${users.size}")
        log.info("Finish: execution - $millis ms")
    }

    @Test
    fun loadContributorsBackground() {
        var users: List<User>
        val millis = measureTimeMillis {
            val func: () -> List<User> = request.loadContributorsBackground("cats")
            users = func.invoke()
            while (users.isEmpty()) {
                TimeUnit.MILLISECONDS.sleep(100)
            }
        }
        log.info("Contributors size: ${users.size}")
        log.info("Finish: execution - $millis ms")
    }

    @Test
    fun loadContributorsNotBlocking() {
        var users: List<User>
        val millis = measureTimeMillis {
            runBlocking {
                users = request.loadContributorsNotBlocking("cats")
            }
        }
        log.info("Contributors size: ${users.size}")
        log.info("Finish: execution - $millis ms")
    }

}
