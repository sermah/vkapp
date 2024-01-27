package com.sermah.vkapp.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val AppType = AppTypography(
    postAuthorName = Typography.titleMedium,
    postTime = Typography.bodyMedium,
    postText = Typography.bodyLarge.copy(
        color = Color.Black,
        lineHeight = 18.sp,
    ),
    postButtons = Typography.bodyMedium,
    postViews = Typography.bodySmall
)

@Immutable
class AppTypography(
    val postAuthorName: TextStyle,
    val postTime: TextStyle,
    val postText: TextStyle,
    val postButtons: TextStyle,
    val postViews: TextStyle,
)