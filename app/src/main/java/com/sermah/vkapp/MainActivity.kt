package com.sermah.vkapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sermah.vkapp.ui.VKWall
import com.sermah.vkapp.ui.data.Post
import com.sermah.vkapp.ui.data.toUIPost
import com.sermah.vkapp.ui.theme.Typography
import com.sermah.vkapp.ui.theme.VKAppTheme
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.newsfeed.NewsfeedService
import com.vk.sdk.api.newsfeed.dto.NewsfeedGenericResponseDto
import com.vk.sdk.api.newsfeed.dto.NewsfeedNewsfeedItemDto
import com.vk.sdk.api.newsfeed.dto.NewsfeedNewsfeedItemTypeDto
import com.vk.sdk.api.wall.WallService
import com.vk.sdk.api.wall.dto.WallGetExtendedResponseDto
import com.vk.sdk.api.wall.dto.WallGetResponseDto
import com.vk.sdk.api.wall.dto.WallWallItemDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    data class VKData(
        val userId: Long = 0L
    )

    private val data = mutableStateOf(VKData(0L))
    private val displayPosts = mutableStateListOf<Post>()
    private val newsfeedService = NewsfeedService()
    private val wallService = WallService()
    private var startFrom = ""
    private var offset = 0

    private val loginCallback =
        ActivityResultCallback<VKAuthenticationResult> { result ->
            if (result is VKAuthenticationResult.Success) {
                data.value = VKData(result.token.userId.value)
                Log.d("MainActivity", "Login succeeded for user ID: " +
                    "${result.token.userId}")
            } else if (result is VKAuthenticationResult.Failed) {
                data.value = VKData(0)
                Log.e("MainActivity", "Login failed: ${result.exception.message}")
            }
        }
    private val loginLauncher = VK.login(this@MainActivity, loginCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VKAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    val uId = data.value.userId
                    if (uId > 0) {
                        Column {
                            Text("Logged in as ID: $uId")

                            VKWall(
                                posts = displayPosts,
                                onLoadMore = { loadWallPosts(coroutineScope) },
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text("VK App", style = Typography.titleMedium)
                            Text("Login PLS")
                            Button(onClick = { loginButtonHandler(coroutineScope) }) {
                                Text("Login")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loginButtonHandler(scope: CoroutineScope) {
        scope.launch {
            loginLauncher.launch(mutableListOf(VKScope.WALL, VKScope.FRIENDS))
        }
    }

//    private fun makePost(by: String, text: String) = Post(
//        authorName = by,
//        authorId = 0,
//        authorPicUrl = "https://sun9-38.userapi.com/impf/t0pCKKPdkY85H-g0Pmz_Mv09DKWmHrZQvOSkEg/SmmY-d5b_t0.jpg?size=766x819&quality=95&sign=cd40fdecd596f67fb6e3decc26d33f82&type=album",
//        timePosted = 420,
//        text = text,
//        attachments = emptyList()
//    )

    private fun loadFeedPosts(scope: CoroutineScope) {
        scope.launch {
            val request = newsfeedService.newsfeedGet(
                filters = listOf(NewsfeedNewsfeedItemTypeDto.POST),
                returnBanned = false,
                count = 30,
                startFrom = startFrom,
            )
            VK.execute(request, object: VKApiCallback<NewsfeedGenericResponseDto> {
                override fun success(result: NewsfeedGenericResponseDto) {
                    Log.d("MainActivity", "Loaded posts")
                    val posts = result.items
                        .filterIsInstance<NewsfeedNewsfeedItemDto.NewsfeedItemWallpostDto>()
                    // TODO: Write Lock
                    displayPosts.addAll(
                        posts.map { it.toUIPost() }
                    )

                    // TODO new startFrom
                }

                override fun fail(error: Exception) {
                    Log.e("MainActivity", "Failed to load posts: ${error.message}")
                }
            })
        }
    }

    private fun loadWallPosts(scope: CoroutineScope) {
        scope.launch {
            val count = 30
            val request = wallService.wallGetExtended(
                ownerId = UserId(1L),
                count = count,
                offset = offset,
                filter = "all",
            )
            VK.execute(request, object: VKApiCallback<WallGetExtendedResponseDto> {
                override fun success(result: WallGetExtendedResponseDto) {
                    Log.d("MainActivity", "Loaded $count posts")
                    Log.d("MainActivity", "Items = ${result.items}")

                    val posts = result.items
                        .filterIsInstance<WallWallItemDto.WallWallpostFullDto>()
                    val users = result.profiles
                    val groups = result.groups
                    // TODO: Write Lock
                    displayPosts.addAll(
                        posts.map { it.toUIPost(users, groups) }
                    )

                    offset += count
                }

                override fun fail(error: Exception) {
                    Log.e("MainActivity", "Failed to load posts: ${error.message}")
                }
            })
        }
    }
}
