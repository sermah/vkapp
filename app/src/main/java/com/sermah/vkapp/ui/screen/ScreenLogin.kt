package com.sermah.vkapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sermah.vkapp.ui.theme.Typography

@Composable
fun ScreenLogin(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {

        Text("VK App", style = Typography.displayMedium)
        Text("You are not logged in.")
        Button(onClick = onLoginClick) {
            Text("Login")
        }
    }
}