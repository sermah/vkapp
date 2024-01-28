package com.sermah.vkapp.ui.data

sealed interface PostAttachment {
    data class Image(val url: String, val id: Long): PostAttachment
    data class Music(val title: String, val author: String, val id: Long): PostAttachment
    data class Video(val title: String, val author: String, val id: Long): PostAttachment
}