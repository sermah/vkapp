package com.sermah.vkapp.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sermah.vkapp.ui.data.Post
import com.sermah.vkapp.ui.data.PostAttachment
import com.sermah.vkapp.ui.theme.AppType
import com.sermah.vkapp.ui.utils.displayCount
import com.sermah.vkapp.ui.utils.displayTime

@Composable
fun Post(
    post: Post,
    onLike: () -> Unit,
    innerPadding: Dp,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(post.id) { mutableStateOf(false) }

//    Card(
//        modifier = modifier,
////        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
        Column(
            modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Post_Header(
                authorName = post.authorName,
                authorId = post.authorId,
                authorPicUrl = post.authorPicUrl,
                timePosted = post.timePosted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = innerPadding),
            )
            Post_Text(
                text = post.text,
                isExpanded = expanded,
                maxLines = 10,
                onShowClick = {
                    expanded = !expanded
                },
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 4.dp)
                    .fillMaxWidth()
                    .padding(horizontal = innerPadding),
            )
            Post_Visuals(
                attachments = post.attachments,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Post_Footer(
                likes = post.likes,
                reposts = post.reposts,
                views = post.views,
                isLiked = post.isLiked,
                onLike = onLike,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = innerPadding),
            )
        }

//    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun Post_Header(
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
fun Post_Footer(
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
            if (views > 0)
                Text(
                    text = "${displayCount(views)} views",
                    style = AppType.postViews,
                )
        }
    }
}

@Composable
fun Post_Text(
    text: String,
    isExpanded: Boolean,
    maxLines: Int,
    modifier: Modifier = Modifier,
    onShowClick: () -> Unit,
) {
    var shouldBeCropped by remember { mutableStateOf(false) }

    Column(modifier) {
        if (text.isNotEmpty()) {
            Text(
                text = text.trim(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = AppType.postText,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (!isExpanded) maxLines else Int.MAX_VALUE,
                modifier = Modifier.animateContentSize(),
                onTextLayout = {
                    shouldBeCropped = it.hasVisualOverflow || it.lineCount > maxLines
                }
            )
            if (shouldBeCropped)
                Text(
                    text = if (!isExpanded) "Show more…" else "Show less…",
                    style = AppType.postMore,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable(onClick = onShowClick),
                )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun Post_Visuals(
    attachments: Collection<PostAttachment>,
    modifier: Modifier,
) {
    val visuals = attachments.filter {
        it is PostAttachment.Photo
//            || it is PostAttachment.Video
    }
    if (visuals.isNotEmpty()) {
        val bgColor = MaterialTheme.colorScheme.secondaryContainer
        val pagerState = rememberPagerState(0, 0f) { visuals.size }

        // aspect ratio is used to calculate visuals height which takes the biggest
        // ratio between 1:1 and 2:1 (w:h)

        val aspectRatio = visuals.minOf {
            if (it is PostAttachment.Photo && it.w > 0)
                it.w.toFloat() / it.h.toFloat()
            else Float.MAX_VALUE
        }.coerceIn(1f /* 1:1 */, 2f /* 2:1 */)

        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = modifier,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .background(bgColor)
                    .aspectRatio(aspectRatio, true)
            ) { idx ->
                Box(
                    contentAlignment = Alignment.Center,
//                    modifier = Modifier.fillMaxHeight()
                ) {
                    when (val visual = visuals[idx]) {
                        is PostAttachment.Photo -> {
                            GlideImage(
                                model = if (visual.url != "") visual.url else null,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                it.fitCenter()
                            }
                        }

                        else -> {}
                    }
                }
            }

            if (pagerState.pageCount > 1)
                Text(
                    text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 12.dp, end = 12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0, 0, 0, 128))
                        .padding(vertical = 6.dp, horizontal = 8.dp)
                )
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun PostPreview() {
    Post(
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
        onLike = {},
        innerPadding = 8.dp,
    )
}

@Preview(widthDp = 360)
@Composable
private fun PostPreview_BigStrings() {
    Post(
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
        onLike = {},
        innerPadding = 8.dp,
    )
}

@Preview(widthDp = 360, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PostPreview_Attachment() {
    Post(
        post = Post(
            id = 1,
            authorName = "My favourite jokes",
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
            attachments = listOf(
                PostAttachment.Photo(
                    id = 0,
                    albumId = 0,
                    ownerId = 1L,
                    ownerName = "",
                    userId = 0,
                    userName = "",
                    text = "",
                    url = "",
                    100,
                    50
                ),
                PostAttachment.Photo(
                    id = 0,
                    albumId = 0,
                    ownerId = 1L,
                    ownerName = "",
                    userId = 0,
                    userName = "",
                    text = "",
                    url = "",
                    50,
                    100
                )
            ),
        ),
        onLike = {},
        innerPadding = 8.dp,
    )
}

