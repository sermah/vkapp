package com.sermah.vkapp

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sermah.vkapp.ui.data.Post
import com.sermah.vkapp.ui.data.UserProfile
import com.sermah.vkapp.ui.data.toUIPost
import com.sermah.vkapp.ui.data.toUserProfile
import com.sermah.vkapp.ui.state.UiState
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.likes.LikesService
import com.vk.sdk.api.likes.dto.LikesAddResponseDto
import com.vk.sdk.api.likes.dto.LikesDeleteResponseDto
import com.vk.sdk.api.newsfeed.NewsfeedService
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFieldsDto
import com.vk.sdk.api.users.dto.UsersUserFullDto
import com.vk.sdk.api.wall.WallService
import com.vk.sdk.api.wall.dto.WallGetExtendedResponseDto
import com.vk.sdk.api.wall.dto.WallWallItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private lateinit var loginLauncher: ActivityResultLauncher<Collection<VKScope>>

    private val newsfeedService = NewsfeedService()
    private val wallService = WallService()
    private val usersService = UsersService()
    private val likesService = LikesService()

    private val _userId = MutableStateFlow(VK.getUserId().value)
    val userId: StateFlow<Long> get() = _userId

    private val _location = MutableStateFlow(Location.PROFILE)
    val location: StateFlow<Location> get() = _location

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> get() = _profile

    private val _posts = MutableStateFlow<List<Post>>(mutableListOf())
    val posts: StateFlow<List<Post>> get() = _posts

    val uiState: StateFlow<UiState> = combine(
        userId, location, profile, posts
    ) { mUserId, mLocation, mProfile, mPosts ->
        if (mUserId == 0L) return@combine UiState.LoggedOut
        return@combine when (mLocation) {
            Location.PROFILE -> UiState.Profile(mProfile, mPosts, mPosts.size)
            Location.FEED -> UiState.Feed(mPosts, mPosts.size)
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = UiState.LoggedOut
        )

    enum class Location {
        PROFILE, FEED,
    }

    init {
        // Flows
        profile.onEach {
            _posts.value = listOf()
            loadWallPosts()
        }.launchIn(viewModelScope)
        location.onEach { _posts.value = listOf() }.launchIn(viewModelScope)

        // VK
        registerTokenExpired()

        // Begin
        loadProfile(1)
        // banned 282075916
        // closed 289800033
    }

    fun openVKLogin() {
        viewModelScope.launch {
            loginLauncher.launch(
                listOf(
                    VKScope.WALL, VKScope.PHOTOS, VKScope.FRIENDS, VKScope.GROUPS,
                )
            )
        }
    }

    fun registerLoginResultLauncher(activity: ComponentActivity) {
        loginLauncher = VK.login(activity, loginCallback)
    }

    private fun loadProfile(id: Long) {
        viewModelScope.launch {
            val request = usersService.usersGet(
                userIds = listOf(UserId(id)),
                fields = listOf(
                    UsersFieldsDto.BLACKLISTED,
                    UsersFieldsDto.PHOTO_200,
                    UsersFieldsDto.SCREEN_NAME,
                    UsersFieldsDto.ONLINE,
                    UsersFieldsDto.LAST_SEEN,
                    UsersFieldsDto.STATUS,
                )
            )
            VK.execute(request, object : VKApiCallback<List<UsersUserFullDto>> {
                override fun success(result: List<UsersUserFullDto>) {
                    Log.d("AppViewModel", "Received ${result.size} user profiles.")

                    if (result.size == 1) {
                        _profile.value = result[0].toUserProfile()
                        Log.d("AppViewModel", "Loaded profile id: ${profile.value!!.id}")
                    } else {
                        Log.e("AppViewModel", "Failed to load profile. Wrong answer size.")
                    }
                }

                override fun fail(error: Exception) {
                    Log.e("AppViewModel", "Failed to load profile: ${error.message}")
                }
            })
        }
    }

    fun registerTokenExpired() {
        VK.addTokenExpiredHandler(object : VKTokenExpiredHandler {
            override fun onTokenExpired() {
                updateUserId()
            }
        })
    }

    fun updateUserId() {
        _userId.value = VK.getUserId().value
    }

    fun loadMorePosts() {
        loadWallPosts()
    }

    private fun loadWallPosts() {
        val state = uiState.value as? UiState.Profile ?: return

        viewModelScope.launch {
            val count = 30
            val request = wallService.wallGetExtended(
                ownerId = UserId(state.profile?.id ?: 0),
                count = count,
                offset = state.offset,
                filter = "all",
            )
            VK.execute(request, object : VKApiCallback<WallGetExtendedResponseDto> {
                override fun success(result: WallGetExtendedResponseDto) {
                    Log.d("AppViewModel", "Loaded $count posts")
                    Log.d("AppViewModel", "Items = ${result.items}")

                    val newPosts = result.items
                        .filterIsInstance<WallWallItemDto.WallWallpostFullDto>()
                    val users = result.profiles.associateBy { it.id }
                    val groups = result.groups.associateBy { it.id }

                    _posts.value = _posts.value + newPosts.map { it.toUIPost(users, groups) }
                }

                override fun fail(error: Exception) {
                    Log.e("AppViewModel", "Failed to load posts: ${error.message}")
                }
            })
        }
    }

    private val loginCallback =
        ActivityResultCallback<VKAuthenticationResult> { result ->
            when (result) {
                is VKAuthenticationResult.Success -> {
                    Log.d(
                        "AppViewModel",
                        "Login succeeded for user ID: ${result.token.userId}"
                    )

                    loadProfile(1)
                }

                is VKAuthenticationResult.Failed -> {
                    Log.e(
                        "AppViewModel",
                        "Login failed: ${result.exception.message}"
                    )
                }
            }

            updateUserId()
        }

    fun likePost(postId: Int) {
        val post = _posts.value.find { it.id == postId } ?: run {
            Log.e("AppViewModel", "Failed to find post with id: $postId")
            return
        }
        if (post.isLiked) deleteLike(post)
        else addLike(post)
    }

    private fun addLike(post: Post) {
        val request = likesService.likesAdd(
            type = "post",
            itemId = post.id,
            ownerId = UserId(post.authorId),
        )

        VK.execute(request, object : VKApiCallback<LikesAddResponseDto> {
            override fun success(result: LikesAddResponseDto) {
                val postIdx = _posts.value.indexOf(post)
                if (postIdx < 0) {
                    Log.e("AppViewModel", "Like add success, but post view is absent.")
                }
                _posts.value = _posts.value.let {
                    val mutable = it.toMutableList()
                    mutable[postIdx] = post.copy(
                        likes = result.likes,
                        isLiked = true
                    )
                    mutable
                }
                Log.w("AppViewModel", "Added like for postId: ${post.id} (likes = ${result.likes})")
            }

            override fun fail(error: Exception) {
                Log.w("AppViewModel", "Failed to add like for postId: ${post.id} - \n$error")
            }
        })
    }

    private fun deleteLike(post: Post) {
        val request = likesService.likesDelete(
            type = "post",
            itemId = post.id,
            ownerId = UserId(post.authorId),
        )

        VK.execute(request, object : VKApiCallback<LikesDeleteResponseDto> {
            override fun success(result: LikesDeleteResponseDto) {
                val postIdx = _posts.value.indexOf(post)
                if (postIdx < 0) {
                    Log.e("AppViewModel", "Like delete success, but post view is absent.")
                }
                _posts.value = _posts.value.let {
                    val mutable = it.toMutableList()
                    mutable[postIdx] = post.copy(
                        likes = result.likes,
                        isLiked = false
                    )
                    mutable
                }
                Log.w(
                    "AppViewModel",
                    "Deleted like for postId: ${post.id} (likes = ${result.likes})"
                )
            }

            override fun fail(error: Exception) {
                Log.w("AppViewModel", "Failed to delete like for postId: ${post.id} - \n$error")
            }
        })
    }

    //    private fun loadFeedPosts(scope: CoroutineScope) {
//        scope.launch {
//            val request = newsfeedService.newsfeedGet(
//                filters = listOf(NewsfeedNewsfeedItemTypeDto.POST),
//                returnBanned = false,
//                count = 30,
//                startFrom = startFrom,
//            )
//            VK.execute(request, object: VKApiCallback<NewsfeedGenericResponseDto> {
//                override fun success(result: NewsfeedGenericResponseDto) {
//                    Log.d("AppViewModel", "Loaded posts")
//                    val posts = result.items
//                        .filterIsInstance<NewsfeedNewsfeedItemDto.NewsfeedItemWallpostDto>()
//                    // TODO: Write Lock
//                    displayPosts.addAll(
//                        posts.map { it.toUIPost() }
//                    )
//
//                    // TODO new startFrom
//                }
//
//                override fun fail(error: Exception) {
//                    Log.e("AppViewModel", "Failed to load posts: ${error.message}")
//                }
//            })
//        }
//    }

//    private fun addPosts(posts: Collection<Post>) = uiStateLock.write {
//
//    }
//
}