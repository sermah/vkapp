package com.sermah.vkapp.ui.data

sealed interface Online {
    data class Now(
        val platform: Int,
    ): Online
    data class Was(
        val time: Int,
        val platform: Int,
    ): Online
}