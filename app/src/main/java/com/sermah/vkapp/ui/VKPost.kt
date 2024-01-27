package com.sermah.vkapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sermah.vkapp.ui.data.Post
import com.sermah.vkapp.ui.theme.AppType
import com.sermah.vkapp.ui.utils.displayCount
import com.sermah.vkapp.ui.utils.displayTime

@Composable
fun VKPost(
    post: Post,
    onLike: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            VKPost_Header(
                authorName = post.authorName,
                authorId = post.authorId,
                authorPicUrl = post.authorPicUrl,
                timePosted = post.timePosted,
                modifier = modifier.fillMaxWidth()
            )
            Text(
                text = post.text.trim(),
                style = AppType.postText,
                modifier = modifier.fillMaxWidth(),
            )
            VKPost_Footer(
                likes = post.likes,
                reposts = post.reposts,
                views = post.views,
                onLike = onLike
            )
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun VKPost_Header(
    authorName: String,
    authorId: Long,
    authorPicUrl: String,
    timePosted: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlideImage(
            model = if (authorPicUrl != "") authorPicUrl else null,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(64.dp, 64.dp)
        ) {
            it.fitCenter().circleCrop()
        }
        Column {
            Text(
                text = authorName,
                style = AppType.postAuthorName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (timePosted != -1)
                    displayTime(timePosted) else "",
                style = AppType.postTime,
            )
        }
    }
}

@Composable
fun VKPost_Footer(
    likes: Int,
    reposts: Int,
    views: Int,
    onLike: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        modifier = modifier.padding(top = 10.dp)
    ) {
        VKPost_Button(
            icon = Icons.Outlined.FavoriteBorder,
            count = likes,
            contentDescription = "Like",
            onClick = onLike
        )
        VKPost_Button(
            icon = Icons.Outlined.Send,
            count = reposts,
            contentDescription = "Repost",
            onClick = {}
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            // TODO Eye Icon
            Text(
                text = "${displayCount(views)} views",
                style = AppType.postViews,
            )
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun VKPostPreview() {
    VKPost(
        post = Post(
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
            attachments = emptyList(),
        ),
        onLike = {}
    )
}

@Preview(widthDp = 360)
@Composable
private fun VKPostPreview_BigStrings() {
    VKPost(
        post = Post(
            authorName = "My favourite jokes group about jokes",
            authorId = 0,
            authorPicUrl = "https://sun9-38.userapi.com/impf/t0pCKKPdkY85H-g0Pmz_Mv09DKWmHrZQvOSkEg/SmmY-d5b_t0.jpg?size=766x819&quality=95&sign=cd40fdecd596f67fb6e3decc26d33f82&type=album",
            timePosted = 420,
            text = "Ullam illo dolores sed. Incidunt voluptates suscipit at quos et ut vitae. Eveniet tenetur qui sunt facilis error. Corrupti ad magni esse consectetur sed possimus odit.\n" +
                "\n" +
                "Illo et impedit earum molestias praesentium tempora voluptatem sequi. Omnis voluptatibus mollitia et non et quibusdam. Et placeat facilis sed. Consequatur nulla mollitia repellendus accusantium. Beatae veritatis sit quis.\n",
            likes = 69_500_000,
            reposts = 10_200,
            views = 2_100_000_000,
            attachments = emptyList(),
        ),
        onLike = {}
    )
}
