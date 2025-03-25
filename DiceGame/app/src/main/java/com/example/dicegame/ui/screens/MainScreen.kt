package com.example.dicegame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegame.ui.theme.LocalThemeState

@Composable
fun MainScreen(
    onNewGameClick: () -> Unit
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    val isDarkTheme = LocalThemeState.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Dice Game",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 32.dp),
                color = if (isDarkTheme.value) Color.White else Color.Black
            )

            Button(
                onClick = onNewGameClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 8.dp)
            ) {
                Text("New Game", fontSize = 18.sp)
            }

            Button(
                onClick = { showAboutDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 8.dp)
            ) {
                Text("About", fontSize = 18.sp)
            }
        }

        // Theme toggle switch at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dark Theme",
                fontSize = 16.sp,
                color = if (isDarkTheme.value) Color.White else Color.Black
            )
            Switch(
                checked = isDarkTheme.value,
                onCheckedChange = { isDarkTheme.value = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About", color = if (isDarkTheme.value) Color.White else Color.Black) },
            text = {
                Text(
                    "Student ID: 2052750\nName: Pasindu Ravisara\n\n" +
                            "I confirm that I understand what plagiarism is and have read and understood " +
                            "the section on assessment offenses in the Essential Information for Students. " +
                            "The work that I have submitted is entirely my own. Any work from other authors " +
                            "is duly referenced and acknowledged.",
                    textAlign = TextAlign.Justify,
                    color = if (isDarkTheme.value) Color.White else Color.Black
                )
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}