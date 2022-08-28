package dev.voroby.coroutines

import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.thread

class Request(private val service: GitHubService) {

    private val log: Logger = LoggerFactory.getLogger(Request::class.java)

    fun loadContributorsBlocking(org: String): List<User> {
        return service.getOrgRepos(org)
            .also { log.info("Loaded repositories for organization: ${it.size}") }
            .flatMap { repo -> service.getRepoContributors(repo).also { log.info("Contributors for repository $repo: ${it.size}") } }

    }


    fun loadContributorsBackground(org: String): () -> List<User> {
        val users: MutableList<User> = mutableListOf()
        thread {
            users.addAll(loadContributorsBlocking(org))
        }

        return { users }
    }

    suspend fun loadContributorsNotBlocking(org: String): List<User> = coroutineScope {
        val repos = async {
            service.getOrgRepos(org)
                .also { log.info("Loaded repositories for organization: ${it.size}") }
        }
        val contributors: MutableList<User> = Collections.synchronizedList(mutableListOf())
        val jobs = mutableListOf<Job>()
        repos.await().forEach {
            val job = launch(Dispatchers.IO) {
                val repo = it
                val users =
                    service.getRepoContributors(repo).also { log.info("Contributors for repository $repo: ${it.size}") }
                contributors.addAll(users)
            }
            jobs.add(job)
        }
        jobs.forEach { it.join() }

        contributors
    }

}
