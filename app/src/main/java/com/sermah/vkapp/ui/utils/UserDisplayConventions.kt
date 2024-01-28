package com.sermah.vkapp.ui.utils

import com.sermah.vkapp.ui.data.UserProfile
import com.vk.sdk.api.users.dto.UsersUserFullDto

val UsersUserFullDto.displayName: String
    get() = "$firstName $lastName"

val UserProfile.displayName: String
    get() = "$firstName $lastName"