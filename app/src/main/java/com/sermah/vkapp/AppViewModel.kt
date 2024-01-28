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

    private val _userId = MutableStateFlow(VK.getUserId().value)
    val userId: StateFlow<Long> get() = _userId

    private val _location = MutableStateFlow(Location.PROFILE)
    val location: StateFlow<Location> get() = _location

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> get() = _profile

    private val _posts = MutableStateFlow<List<Post>>(listOf())
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
        PROFILE, FEED
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
                    Log.d("MainActivityViewModel", "Received ${result.size} user profiles.")

                    if (result.size == 1) {
                        _profile.value = result[0].toUserProfile()
                        Log.d("MainActivityViewModel", "Loaded profile id: ${profile.value!!.id}")
                    } else {
                        Log.e("MainActivityViewModel", "Failed to load profile. Wrong answer size.")
                    }
                }

                override fun fail(error: Exception) {
                    Log.e("MainActivityViewModel", "Failed to load profile: ${error.message}")
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
                    Log.d("MainActivityViewModel", "Loaded $count posts")
                    Log.d("MainActivityViewModel", "Items = ${result.items}")

                    val newPosts = result.items
                        .filterIsInstance<WallWallItemDto.WallWallpostFullDto>()
                    val users = result.profiles
                    val groups = result.groups

                    _posts.value = _posts.value + newPosts.map { it.toUIPost(users, groups) }
                }

                override fun fail(error: Exception) {
                    Log.e("MainActivityViewModel", "Failed to load posts: ${error.message}")
                }
            })
        }
    }

    private val loginCallback =
        ActivityResultCallback<VKAuthenticationResult> { result ->
            when (result) {
                is VKAuthenticationResult.Success -> {
                    Log.d(
                        "MainActivityViewModel",
                        "Login succeeded for user ID: ${result.token.userId}"
                    )
                }

                is VKAuthenticationResult.Failed -> {
                    Log.e(
                        "MainActivityViewModel",
                        "Login failed: ${result.exception.message}"
                    )
                }
            }

            updateUserId()
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
//                    Log.d("MainActivityViewModel", "Loaded posts")
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
//                    Log.e("MainActivityViewModel", "Failed to load posts: ${error.message}")
//                }
//            })
//        }
//    }

//    private fun addPosts(posts: Collection<Post>) = uiStateLock.write {
//
//    }
//
}