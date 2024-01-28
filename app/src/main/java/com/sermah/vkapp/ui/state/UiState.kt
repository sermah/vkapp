package com.sermah.vkapp.ui.state

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.sermah.vkapp.ui.data.Post
import com.sermah.vkapp.ui.data.UserProfile

sealed interface UiState {
    data object LoggedOut: UiState

    data class Profile(
        val profile: UserProfile?,
        val posts: List<Post>,
        val offset: Int,
    ): UiState

    data class Feed(
        val posts: List<Post>,
        val offset: Int,
    ): UiState
}