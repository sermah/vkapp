package com.sermah.vkapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sermah.vkapp.AppViewModel
import com.sermah.vkapp.ui.screen.ScreenLogin
import com.sermah.vkapp.ui.screen.ScreenUserProfile
import com.sermah.vkapp.ui.state.UiState
import com.sermah.vkapp.ui.theme.VKAppTheme

@Composable
fun VKApp(viewModel: AppViewModel) = VKAppTheme {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        when (uiState) {
            is UiState.Profile -> {
                ScreenUserProfile(
                    profile = (uiState as UiState.Profile).profile,
                    posts = (uiState as UiState.Profile).posts,
                    onLoadMorePosts = viewModel::loadMorePosts,
                    onLikePost = viewModel::likePost,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                )
            }

            is UiState.LoggedOut -> {
                ScreenLogin(
                    onLoginClick = { viewModel.openVKLogin() },
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                )
            }

            is UiState.Feed -> {

            }
        }
    }
}