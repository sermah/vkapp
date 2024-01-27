package com.sermah.vkapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val commonPostText = Typography.bodyLarge.copy(
    lineHeight = 18.sp,
)

val AppType = AppTypography(
    postAuthorName = Typography.titleMedium,
    postTime = Typography.bodyMedium,
    postText = commonPostText,
    postMore = commonPostText,
    postLink = commonPostText,
    postButtons = Typography.bodyMedium,
    postViews = Typography.bodySmall,
)

@Immutable
class AppTypography(
    val postAuthorName: TextStyle,
    val postTime: TextStyle,
    val postText: TextStyle,
    val postMore: TextStyle,
    val postLink: TextStyle,
    val postButtons: TextStyle,
    val postViews: TextStyle,
)