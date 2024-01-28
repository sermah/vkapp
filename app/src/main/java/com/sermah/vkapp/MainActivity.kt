package com.sermah.vkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.sermah.vkapp.ui.VKApp

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.registerLoginResultLauncher(this)

        setContent { VKApp(viewModel = viewModel) }
    }
}
