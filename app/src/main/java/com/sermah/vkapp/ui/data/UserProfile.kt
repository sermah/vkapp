package com.sermah.vkapp.ui.data

import android.util.Log
import com.vk.sdk.api.users.dto.UsersUserFullDto

sealed interface UserProfile {

    val id: Long
    val firstName: String
    val lastName: String
    val photoUrl: String

    data class Open(
        override val id: Long,
        override val firstName: String,
        override val lastName: String,
        override val photoUrl: String,
        val screenName: String,
        val online: Online,
        val status: String,
    ) : UserProfile

    data class Closed(
        override val id: Long,
        override val firstName: String,
        override val lastName: String,
        override val photoUrl: String,
    ) : UserProfile

    data class Blacklisted(
        override val id: Long,
        override val firstName: String,
        override val lastName: String,
        override val photoUrl: String,
    ) : UserProfile

    data class Deactivated(
        override val id: Long,
        override val firstName: String,
        override val lastName: String,
        override val photoUrl: String,
        val reason: Reason
    ) : UserProfile {
        enum class Reason {
            DELETED,
            BANNED;

            companion object {
                fun from(str: String) = if (str == "banned") BANNED else DELETED
            }
        }
    }
}

fun UsersUserFullDto.toUserProfile(): UserProfile {
    val id = this.id.value
    val firstName = this.firstName ?: ""
    val lastName = this.lastName ?: ""
    val photoUrl = this.photo200 ?: ""

    if (this.deactivated != null) return UserProfile.Deactivated(
        id, firstName, lastName, photoUrl,
        reason = UserProfile.Deactivated.Reason.from(this.deactivated!!)
    ) else if (this.canAccessClosed == false) return UserProfile.Closed(
        id, firstName, lastName, photoUrl,
    ) else if (this.blacklisted?.value == 1) return UserProfile.Blacklisted(
        id, firstName, lastName, photoUrl,
    ) else return UserProfile.Open(
        id, firstName, lastName, photoUrl,
        screenName = this.screenName ?: "",
        online = if (this.online?.value != 0)
            Online.Now(0) // TODO App ID
        else Online.Was(
            time = this.lastSeen?.time ?: 0,
            platform = this.lastSeen?.platform ?: 0,
        ),
        status = this.status ?: "",
    ).also { Log.d("UserProfile", "Converted profile: $it") }

}


