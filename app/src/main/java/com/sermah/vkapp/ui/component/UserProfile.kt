package com.sermah.vkapp.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
    modifier: Modifier = Modifier,
    profile: UserProfile,
    shrinkFactor: Float = 1f, // 0 = shrinked, 1 = opened
    cardColor: Color = MaterialTheme.colorScheme.background,
    onCardColor: Color = MaterialTheme.colorScheme.onBackground,
    onShortNameClick: () -> Unit = {},
) {
    val photoSize = 96.dp
    val cornerSize = 0.dp

    Card(
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = cornerSize,
            bottomEnd = cornerSize,
        ),
        modifier = modifier,
    ) {
        Box {
            Column(modifier = Modifier.alpha(shrinkFactor)) {
                UserProfile_Head(
                    profile, photoSize,
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                UserProfile_Info(profile, Modifier.fillMaxWidth())
            }
            Text(
                modifier = Modifier
                    .alpha(1 - shrinkFactor)
                    .padding(16.dp)
                    .clickable { onShortNameClick() },
                text = profile.screenName,
                style = AppType.appBarTitle,
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun UserProfile_Head(
    profile: UserProfile,
    photoSize: Dp,
    modifier: Modifier = Modifier,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 4.dp, bottom = 4.dp),
    ) {
        GlideImage(
            model = if (profile.photoUrl != "") profile.photoUrl else null,
            contentDescription = null,
            modifier = Modifier
                .requiredSize(photoSize)
                .padding(bottom = 4.dp)
        ) {
            it.fitCenter().circleCrop()
        }
        Text(
            text = profile.displayName,
            style = AppType.profileName,
            textAlign = TextAlign.Center,
        )
        if (profile is UserProfile.Open) {
            Text(
                text = when (profile.online) {
                    is Online.Now -> "online"
                    is Online.Was ->
                        if (profile.online.time > 0)
                            "was online ${displayTime(profile.online.time)}"
                        else "offline"
                },
                style = AppType.profileTime,
                textAlign = TextAlign.Center,
            )
            if (profile.status.isNotBlank()) {
                Text(
                    text = profile.status.trim(),
                    style = AppType.profileStatus,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
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
                is UserProfile.Deactivated -> "Account was ${profile.reason.toLower()}."
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

@Preview(widthDp = 360)
@Composable
fun UserProfilePreview() {
    UserProfile(
        profile = UserProfile.Open(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            photoUrl = "",
            screenName = "peedee",
            online = Online.Now(0),
            status = "Hello world!",
        )
    )
}

@Preview(widthDp = 360)
@Composable
fun UserProfilePreview_LongStrings() {
    UserProfile(
        profile = UserProfile.Open(
            id = 1,
            firstName = "Johnathan Badminton",
            lastName = "Goodminton Senior",
            photoUrl = "",
            screenName = "peedeeaf_file_not_found_hello",
            online = Online.Was(420, 0),
            status = "Hello world! I am Johnathan Goodminton Senior and I like my life and I like my kids and I like my wife.",
        )
    )
}
