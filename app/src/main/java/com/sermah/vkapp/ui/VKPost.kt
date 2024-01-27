package com.sermah.vkapp.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var expanded by remember(post.id) { mutableStateOf(false) }

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
            VKPost_Text(
                text = post.text,
                isExpanded = expanded,
                maxLines = 10,
                onShowClick = {
                    expanded = !expanded
                }
            )
            VKPost_Footer(
                likes = post.likes,
                reposts = post.reposts,
                views = post.views,
                isLiked = post.isLiked,
                onLike = onLike,
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
    val photoSize = 56.dp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(bottom = 4.dp)
    ) {
        GlideImage(
            model = if (authorPicUrl != "") authorPicUrl else null,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(photoSize)
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
    isLiked: Boolean,
    onLike: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        modifier = modifier.padding(top = 6.dp)
    ) {
        VKPost_Button(
            icon = Icons.Outlined.FavoriteBorder,
            count = likes,
            toggle = isLiked,
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

@Composable
fun VKPost_Text(
    text: String,
    isExpanded: Boolean,
    maxLines: Int,
    modifier: Modifier = Modifier,
    onShowClick: () -> Unit,
) {
    var shouldBeCropped by remember { mutableStateOf(false) }

    Text(
        text = text.trim(),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        style = AppType.postText,
        overflow = TextOverflow.Ellipsis,
        maxLines = if (!isExpanded) maxLines else Int.MAX_VALUE,
        modifier = modifier.animateContentSize(),
        onTextLayout = {
            shouldBeCropped = it.hasVisualOverflow || it.lineCount > maxLines
        }
    )
    if (shouldBeCropped)
        Text(
            text = if (!isExpanded) "Show more..." else "Show less...",
            style = AppType.postText,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onShowClick),
        )
}

@Preview(widthDp = 360)
@Composable
private fun VKPostPreview() {
    VKPost(
        post = Post(
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
        ),
        onLike = {}
    )
}

@Preview(widthDp = 360)
@Composable
private fun VKPostPreview_BigStrings() {
    VKPost(
        post = Post(
            id = 1,
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
            isLiked = true,
            attachments = emptyList(),
        ),
        onLike = {}
    )
}
