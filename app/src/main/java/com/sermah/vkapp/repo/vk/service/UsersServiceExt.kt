package com.sermah.vkapp.repo.vk.service

import com.google.gson.reflect.TypeToken
import com.sermah.vkapp.repo.vk.model.UserIdExt
import com.vk.api.sdk.requests.VKRequest
import com.vk.sdk.api.GsonHolder
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFieldsDto
import com.vk.sdk.api.users.dto.UsersUserFullDto

/** usersGet but with screen_names allowed in userIds */
fun UsersService.usersGetExt(
    userIds: List<UserIdExt>? = null,
    fields: List<UsersFieldsDto>? = null,
    nameCase: String? = null
): VKRequest<List<UsersUserFullDto>> = VKApiRequest("users.get") {
    GsonHolder.gson.fromJson(it, TypeToken.get(Array<UsersUserFullDto>::class.java)).toList()
}
    .apply {
        userIds?.let { addParam("user_ids", it, min = 1) }
        val fieldsJsonConverted = fields?.map {
            it.value
        }
        fieldsJsonConverted?.let { addParam("fields", it) }
        nameCase?.let { addParam("name_case", it) }
    }
