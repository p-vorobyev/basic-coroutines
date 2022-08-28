package dev.voroby.coroutines

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream

class GitHubService {

    private val orgReposDB: MutableMap<String, List<String>> = ConcurrentHashMap()

    private val namedReposDB: MutableMap<String, Repo> = ConcurrentHashMap()

    init {
        val catOrg = "cats"
        val dogOrg = "dogs"
        val catRepos = IntStream.range(1, 101).mapToObj { "CatRepo$it" }.toList()
        val dogRepos = IntStream.range(1, 201).mapToObj { "DogRepo$it" }.toList()
        orgReposDB[catOrg] = catRepos
        orgReposDB[dogOrg] = dogRepos
        catRepos.parallelStream().forEach {
            val contributors: MutableSet<User> = mutableSetOf(User(), User(), User())
            namedReposDB[it] = Repo(it, "https://$it", catOrg, contributors)
        }
        dogRepos.parallelStream().forEach {
            val contributors: MutableSet<User> = mutableSetOf(User(), User(), User())
            namedReposDB[it] = Repo(it, "https://$it", dogOrg, contributors)
        }
    }

    fun getOrgRepos(org: String): List<String> {
        TimeUnit.MILLISECONDS.sleep(50)
        return orgReposDB[org] ?: emptyList()
    }

    fun getRepoContributors(repoName: String): List<User> {
        TimeUnit.MILLISECONDS.sleep(50)
        val repo: Repo? = namedReposDB[repoName]
        return repo?.contributors?.toList() ?: emptyList()
    }

}
