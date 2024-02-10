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
        val w: Int,
        val h: Int,
    ) : PostAttachment

    data class Music(val title: String, val author: String, val id: Int) : PostAttachment
    data class Video(
        val id: Int,
        val ownerId: Long,
        val ownerName: String,
        val userId: Long,
        val userName: String,
        val title: String,
        val description: String,
        val imageUrl: String,
        val imageW: Int,
        val imageH: Int,
        val date: Int,
        val player: String,
    ) : PostAttachment

    data class Unknown(val type: String) : PostAttachment
}