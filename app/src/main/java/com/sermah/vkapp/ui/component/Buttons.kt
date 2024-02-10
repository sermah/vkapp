package com.sermah.vkapp.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sermah.vkapp.ui.theme.AppType
import com.sermah.vkapp.ui.utils.displayCount

/**
 * Like, repost, comment buttons
 * @param toggleColor - if set, button will be colored (like if you pressed a "like" button)
 * */
@Composable
fun VKPost_Button(
    icon: ImageVector,
    iconToggled: ImageVector? = null,
    count: Int = 0,
    toggle: Boolean = false,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val paddingValues = PaddingValues(
        start = 14.dp,
        top = 4.dp,
        end = 14.dp,
        bottom = 4.dp
    )
    val animatedContainerColor = animateColorAsState(
        targetValue = if (toggle)
            MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primaryContainer,
        animationSpec = tween(COLOR_TWEEN_DURATION_MS, 0, LinearEasing),
        label = "",
    )
    val animatedContentColor = animateColorAsState(
        targetValue = if (toggle)
            MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onPrimaryContainer,
        animationSpec = tween(COLOR_TWEEN_DURATION_MS, 0, LinearEasing),
        label = "",
    )
    val animatedOpacity = animateFloatAsState(
        targetValue = if (toggle) 1f else 0f,
        animationSpec = tween(COLOR_TWEEN_DURATION_MS, 0, LinearEasing),
        label = "",
    )


    val content: @Composable (RowScope.() -> Unit) = {
        Box {
            Icon(
                icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp),
            )
            if (iconToggled != null)
                Icon(
                    iconToggled,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .size(20.dp)
                        .alpha(animatedOpacity.value),
                )
        }

        if (count > 0)
            Text(
                text = " " + displayCount(count),
                style = AppType.postButtons,
            )
    }

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedContainerColor.value,
            contentColor = animatedContentColor.value,
        ),
        onClick = onClick,
        modifier = modifier,
        contentPadding = paddingValues,
        content = content,
    )
}

private const val COLOR_TWEEN_DURATION_MS = 100

@Preview(
    showBackground = true,
    backgroundColor = 0xFFE0E0E0,
)
@Composable
fun VKPost_ButttonPreview_on() {
    VKPost_Button(
        icon = Icons.Outlined.Check,
        count = 1234,
        toggle = true,
        onClick = {},
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFE0E0E0,
)
@Composable
fun VKPost_ButttonPreview_off() {
    VKPost_Button(
        icon = Icons.Outlined.Check,
        count = 1234,
        toggle = false,
        onClick = {},
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFE0E0E0,
)
@Composable
fun VKPost_ButttonPreview_on_withIcon() {
    VKPost_Button(
        icon = Icons.Outlined.FavoriteBorder,
        iconToggled = Icons.Filled.Favorite,
        count = 1234,
        toggle = true,
        onClick = {},
    )
}
