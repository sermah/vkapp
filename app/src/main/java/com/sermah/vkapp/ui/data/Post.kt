package com.sermah.vkapp.ui.data

import android.util.Log
import com.sermah.vkapp.ui.utils.displayName
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.groups.dto.GroupsGroupFullDto
import com.vk.sdk.api.newsfeed.dto.NewsfeedNewsfeedItemDto
import com.vk.sdk.api.photos.dto.PhotosPhotoSizesTypeDto
import com.vk.sdk.api.users.dto.UsersUserFullDto
import com.vk.sdk.api.wall.dto.WallWallItemDto
import com.vk.sdk.api.wall.dto.WallWallpostAttachmentTypeDto

data class Post(
    val id: Int,
    val authorName: String,
    val authorId: Long,
    val authorPicUrl: String,
    val timePosted: Int,
    val text: String,
    val likes: Int,
    val reposts: Int,
    val views: Int,
    val isLiked: Boolean,
    val attachments: Collection<PostAttachment>
)

fun NewsfeedNewsfeedItemDto.NewsfeedItemWallpostDto.toUIPost() = Post(
    id = this.id ?: -1,
    authorName = this.ownerId.toString(),
    authorId = this.ownerId?.value ?: -1L,
    authorPicUrl = "",
    timePosted = this.date,
    text = this.text ?: "",
    likes = this.likes?.count ?: 0,
    reposts = this.reposts?.count ?: 0,
    views = this.views?.count ?: 0,
    isLiked = this.likes?.userLikes?.value == 1,
    attachments = listOf(),
)

fun WallWallItemDto.WallWallpostFullDto.toUIPost(
    users: Map<UserId, UsersUserFullDto>,
    groups: Map<UserId, GroupsGroupFullDto>,
): Post {
    val user = users[this.ownerId ?: 0L]
    val group = if (user == null) groups[this.ownerId ?: 0L] else null
    return Post(
        id = this.id ?: -1,
        authorName = user?.displayName ?: group?.name ?: "Unknown",
        authorId = this.ownerId?.value ?: -1L,
        authorPicUrl = user?.photo100 ?: group?.photo100 ?: "",
        timePosted = this.date ?: -1,
        text = this.text ?: "",
        likes = this.likes?.count ?: 0,
        reposts = this.reposts?.count ?: 0,
        views = this.views?.count ?: 0,
        isLiked = this.likes?.userLikes?.value == 1,
        attachments = this.attachments?.let { list ->
            list.map { attachment ->
                when (attachment.type) {
                    WallWallpostAttachmentTypeDto.PHOTO -> (attachment.photo).let { photo ->
                        val size = photo?.sizes
                            ?.find { it.type == PhotosPhotoSizesTypeDto.Y }
                        PostAttachment.Photo(
                            id = photo?.id ?: -1,
                            albumId = photo?.albumId ?: -1,
                            ownerId = photo?.ownerId?.value ?: -1L,
                            ownerName = users[photo?.ownerId]?.displayName ?: "Unknown owner",
                            userId = photo?.userId?.value ?: -1L,
                            userName = users[photo?.userId]?.displayName ?: "Unknown user",
                            text = photo?.text ?: "",
                            url = size?.url ?: "",
                            w = size?.width ?: 0,
                            h = size?.height ?: 0,
                        )
                    }

                    WallWallpostAttachmentTypeDto.VIDEO -> (attachment.video).let { video ->
                        val image = video?.image
                            ?.sortedBy { it.width }
                            ?.let {
                                it.firstOrNull { it.width > 1000 } ?: it.last()
                            }

                        PostAttachment.Video(
                            id = video?.id ?: -1,
                            ownerId = video?.ownerId?.value ?: -1L,
                            ownerName = users[video?.ownerId]?.displayName ?: "Unknown owner",
                            userId = video?.userId?.value ?: -1L,
                            userName = users[video?.userId]?.displayName ?: "Unknown user",
                            title = video?.title ?: "Untitled",
                            description = video?.description ?: "",
                            imageUrl = image?.url ?: "",
                            imageW = image?.width ?: 0,
                            imageH = image?.height ?: 0,
                            date = video?.date ?: 0,
                            player = video?.player ?: "",
                        )
                    }

                    else -> PostAttachment.Unknown(attachment.type.value).also {
                        Log.d("data/Post", "Unknown attach type: ${it.type}")
                    }
                }
            }
        } ?: emptyList(),
    )
}
