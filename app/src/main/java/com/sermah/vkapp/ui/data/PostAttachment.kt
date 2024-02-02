package com.sermah.vkapp.ui.data

sealed interface PostAttachment {
    data class Photo(
        val id: Int,
        val albumId: Int,
        val ownerId: Long,
        val ownerName: String,
        val userId: Long,
        val userName: String,
        val text: String,
        val url: String,
    ) : PostAttachment

    data class Music(val title: String, val author: String, val id: Int) : PostAttachment
    data class Video(val title: String, val author: String, val id: Int) : PostAttachment

    data class Unknown(val type: String) : PostAttachment
}