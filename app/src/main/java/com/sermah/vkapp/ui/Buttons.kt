package com.sermah.vkapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    count: Int = 0,
    toggleColor: Color? = null,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val content: @Composable() (RowScope.() -> Unit) = {
        Icon(
            icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
        )
        if (count > 0)
            Text(
                text = " " + displayCount(count),
                style = AppType.postButtons,
            )
    }
    val paddingValues = PaddingValues(
        start = 14.dp,
        top = 4.dp,
        end = 14.dp,
        bottom = 4.dp
    )
    if (toggleColor != null)
        Button(
            onClick = onClick,
            modifier = modifier,
            contentPadding = paddingValues,
            content = content,
        )
    else
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            contentPadding = paddingValues,
            content = content,
        )
}

