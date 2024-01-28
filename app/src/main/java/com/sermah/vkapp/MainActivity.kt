package com.sermah.vkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sermah.vkapp.ui.screen.ScreenLogin
import com.sermah.vkapp.ui.screen.ScreenUserProfile
import com.sermah.vkapp.ui.state.UiState
import com.sermah.vkapp.ui.theme.VKAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.registerLoginResultLauncher(this)

        setContent {
            VKAppTheme {
                val uiState by viewModel.uiState.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (uiState) {
                        is UiState.Profile -> {
                            ScreenUserProfile(
                                profile = (uiState as UiState.Profile).profile,
                                posts = (uiState as UiState.Profile).posts,
                                onLoadMorePosts = { viewModel.loadMorePosts() })
                        }

                        is UiState.LoggedOut -> {
                            ScreenLogin(onLoginClick = { viewModel.openVKLogin() })
                        }

                        is UiState.Feed -> {

                        }
                    }
                }
            }
        }
    }
}
