package com.sermah.vkapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

private val commonPostText = Typography.bodyLarge.copy(
    lineHeight = 18.sp,
)

val AppType = AppTypography(
    postAuthorName = Typography.titleMedium,
    postTime = Typography.bodyMedium,
    postText = commonPostText,
    postMore = commonPostText.copy(
        textDecoration = TextDecoration.Underline,
    ),
    postLink = commonPostText,
    postButtons = Typography.bodyMedium,
    postViews = Typography.bodySmall,

    profileName = Typography.headlineMedium,
    profileTime = Typography.bodyMedium,
    profileStatus = commonPostText,
    profileReason = Typography.bodyMedium,
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

    val profileName: TextStyle,
    val profileTime: TextStyle,
    val profileStatus: TextStyle,
    val profileReason: TextStyle,
)