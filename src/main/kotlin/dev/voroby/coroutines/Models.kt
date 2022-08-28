package dev.voroby.coroutines

import java.util.UUID

data class Repo(
    val name: String,
    val url: String,
    val owner: String,
    val contributors: MutableSet<User> = mutableSetOf()
)

data class User(val username: String = UUID.randomUUID().toString())

