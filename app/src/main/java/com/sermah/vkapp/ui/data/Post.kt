package com.sermah.vkapp.ui.data

import com.sermah.vkapp.ui.utils.displayName
import com.vk.sdk.api.groups.dto.GroupsGroupFullDto
import com.vk.sdk.api.newsfeed.dto.NewsfeedNewsfeedItemDto
import com.vk.sdk.api.users.dto.UsersUserFullDto
import com.vk.sdk.api.wall.dto.WallWallItemDto

data class Post(
    val authorName: String,
    val authorId: Long,
    val authorPicUrl: String,
    val timePosted: Int,
    val text: String,
    val likes: Int,
    val reposts: Int,
    val views: Int,
    val attachments: Collection<PostAttachment>
)

fun NewsfeedNewsfeedItemDto.NewsfeedItemWallpostDto.toUIPost() = Post(
    authorName = this.ownerId.toString(),
    authorId = this.ownerId?.value ?: -1L,
    authorPicUrl = "",
    timePosted = this.date,
    text = this.text ?: "",
    likes = this.likes?.count ?: 0,
    reposts = this.reposts?.count ?: 0,
    views = this.views?.count ?: 0,
    attachments = listOf(),
)

fun WallWallItemDto.WallWallpostFullDto.toUIPost(
    users: Collection<UsersUserFullDto>,
    groups: Collection<GroupsGroupFullDto>,
): Post {
    val user = users.find { it.id == this.ownerId }
    val group = if (user == null) groups.find { it.id == this.ownerId } else null
    return Post(
        authorName = user?.displayName ?: group?.name ?: "Unknown",
        authorId = this.ownerId?.value ?: -1L,
        authorPicUrl = user?.photo100 ?: group?.photo100 ?: "",
        timePosted = this.date ?: -1,
        text = this.text ?: "",
        likes = this.likes?.count ?: 0,
        reposts = this.reposts?.count ?: 0,
        views = this.views?.count ?: 0,
        attachments = listOf(),
    )
}
