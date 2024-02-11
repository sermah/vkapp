package com.sermah.vkapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenFriends(
    modifier: Modifier = Modifier,
    onVisitPerson: (String) -> Unit,
) {
    // TODO Remove sometime
    Column(modifier) {
        Temp_GotoPerson(
            onVisitPerson = onVisitPerson,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun Temp_GotoPerson(
    modifier: Modifier = Modifier,
    onVisitPerson: (String) -> Unit,
) {
    var textInput by remember { mutableStateOf("") }
    Column(modifier) {
        Text(
            text = "Visit page of (id/shortname):",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = textInput,
                onValueChange = {
                    textInput = it
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            Button(
                enabled = textInput.isNotBlank(),
                onClick = {
                    if (textInput.isNotBlank())
                        onVisitPerson(textInput.trim())
                }
            ) {
                Text(text = "Go")
            }
        }
    }
}