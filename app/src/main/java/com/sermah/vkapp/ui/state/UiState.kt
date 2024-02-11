package com.sermah.vkapp.ui.state

import com.sermah.vkapp.ui.data.Post
import com.sermah.vkapp.ui.data.UserProfile

sealed interface UiState {
    data object LoggedOut : UiState

    data object Friends : UiState

    data class Profile(
        val profile: UserProfile?,
        val posts: List<Post>,
        val offset: Int,
    ) : UiState

    data class Feed(
        val posts: List<Post>,
        val offset: Int,
    ): UiState

    fun toTitle(): String =
        when(this) {
            is LoggedOut -> "Login"
            is Profile -> profile?.screenName ?: "Profile"
            is Feed -> "Feed"
            is Friends -> "Friends"
        }
}