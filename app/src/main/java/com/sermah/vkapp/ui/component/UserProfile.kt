package com.sermah.vkapp.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sermah.vkapp.ui.data.Online
import com.sermah.vkapp.ui.data.UserProfile
import com.sermah.vkapp.ui.theme.AppType
import com.sermah.vkapp.ui.utils.displayName
import com.sermah.vkapp.ui.utils.displayTime

@Composable
fun UserProfile(
    profile: UserProfile,
    modifier: Modifier = Modifier,
) {
    val photoSize = 72.dp

    Card(
        shape = RectangleShape,
        modifier = modifier,
    ) {
        UserProfile_Head(profile, photoSize, Modifier.fillMaxWidth().padding(16.dp))
        UserProfile_Info(profile, Modifier.fillMaxWidth())
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun UserProfile_Head(
    profile: UserProfile,
    photoSize: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(bottom = 4.dp)
    ) {
        GlideImage(
            model = if (profile.photoUrl != "") profile.photoUrl else null,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(photoSize)
        ) {
            it.fitCenter().circleCrop()
        }
        Column {
            Text(
                text = profile.displayName,
                style = AppType.profileName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (profile is UserProfile.Open) {
                Text(
                    text = when (profile.online) {
                        is Online.Now -> "Online"
                        is Online.Was -> "was online ${displayTime(profile.online.time)}"
                    },
                    style = AppType.profileTime,
                )
            }
        }
    }
}

@Composable
private fun UserProfile_Info(
    profile: UserProfile,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        if (profile !is UserProfile.Open) {
            val message: String = when (profile) {
                is UserProfile.Blacklisted -> "User blacklisted you."
                is UserProfile.Deactivated -> "Account was ${profile.reason}."
                is UserProfile.Closed -> "This account is closed."
                else -> ""
            }
            Text(
                text = message,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = AppType.profileReason,
            )
        }
    }
}