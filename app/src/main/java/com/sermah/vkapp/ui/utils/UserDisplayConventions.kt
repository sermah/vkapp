package com.sermah.vkapp.ui.utils

import com.vk.sdk.api.users.dto.UsersUserFullDto

val UsersUserFullDto.displayName: String
    get() = "$firstName $lastName"