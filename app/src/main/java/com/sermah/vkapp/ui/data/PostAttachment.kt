package com.sermah.vkapp.ui.data

sealed class PostAttachment {
    data class Image(val url: String, val id: Long)
    data class Music(val title: String, val author: String, val id: Long)
    data class Video(val title: String, val author: String, val id: Long)
}