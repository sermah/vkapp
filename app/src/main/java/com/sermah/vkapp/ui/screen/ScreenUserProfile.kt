package com.sermah.vkapp.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sermah.vkapp.ui.component.UserProfile
import com.sermah.vkapp.ui.component.Wall
import com.sermah.vkapp.ui.data.Online
import com.sermah.vkapp.ui.data.Post
import com.sermah.vkapp.ui.data.UserProfile

@Composable
fun ScreenUserProfile(
    profile: UserProfile?,
    posts: List<Post>,
    onLoadMorePosts: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        if (profile != null) {
            UserProfile(
                profile = profile,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Wall(posts = posts, onLoadMore = onLoadMorePosts)
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(Modifier.size(48.dp))
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(widthDp = 360)
@Composable
private fun ScreenUserProfilePreview() {
    ScreenUserProfile(
        profile = UserProfile.Open(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            photoUrl = "",
            screenName = "pd",
            online = Online.Now(0),
            status = "Hello!",
        ),
        posts = mutableStateListOf(Post(
            id = 0,
            authorName = "John Doe",
            authorId = 0,
            authorPicUrl = "https://sun9-38.userapi.com/impf/t0pCKKPdkY85H-g0Pmz_Mv09DKWmHrZQvOSkEg/SmmY-d5b_t0.jpg?size=766x819&quality=95&sign=cd40fdecd596f67fb6e3decc26d33f82&type=album",
            timePosted = 420,
            text = "Ullam illo dolores sed. Incidunt voluptates suscipit at quos et ut vitae. Eveniet tenetur qui sunt facilis error. Corrupti ad magni esse consectetur sed possimus odit.\n" +
                "\n" +
                "Illo et impedit earum molestias praesentium tempora voluptatem sequi. Omnis voluptatibus mollitia et non et quibusdam. Et placeat facilis sed. Consequatur nulla mollitia repellendus accusantium. Beatae veritatis sit quis.\n",
            likes = 69,
            reposts = 10,
            views = 200,
            isLiked = false,
            attachments = emptyList(),
        )),
        onLoadMorePosts = {},
    )
}