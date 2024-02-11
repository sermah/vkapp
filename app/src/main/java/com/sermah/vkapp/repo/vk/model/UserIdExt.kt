package com.sermah.vkapp.repo.vk.model

sealed interface UserIdExt {
    data class Id(
        val value: Long,
    ) : UserIdExt

    data class Name(
        val value: String,
    ) : UserIdExt
}

fun userIdOf(value: Long) = UserIdExt.Id(value)
fun userIdOf(value: String) = UserIdExt.Name(value)
