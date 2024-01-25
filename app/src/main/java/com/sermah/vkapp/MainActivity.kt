package com.sermah.vkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sermah.vkapp.ui.theme.Typography
import com.sermah.vkapp.ui.theme.VKAppTheme
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    data class VKData(
        val userId: Long = 0L
    )

    private val data = mutableStateOf(VKData(0L))

    private val loginCallback =
        ActivityResultCallback<VKAuthenticationResult> { result ->
            if (result is VKAuthenticationResult.Success)
                data.value = VKData(result.token.userId.value)
            else
                data.value = VKData(0)
        }
    private val loginLauncher = VK.login(this@MainActivity, loginCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VKAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val coroutineScope = rememberCoroutineScope()

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Text("VK App", style = Typography.titleMedium)
                        data.value.userId.let {
                            if (0L == (it ?: 0L)) Text("Login PLS")
                            else Text("Logged in as ID: $it")
                        }
                        Button(onClick = { loginButtonHandler(coroutineScope) }) {
                            Text("Login")
                        }
                    }
                }
            }
        }
    }

    private fun loginButtonHandler(scope: CoroutineScope) {
        scope.launch {
            loginLauncher.launch(mutableListOf(VKScope.WALL, VKScope.PHOTOS))
        }
    }
}
