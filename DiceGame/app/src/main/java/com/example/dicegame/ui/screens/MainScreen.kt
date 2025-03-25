package com.example.dicegame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegame.ui.theme.LocalThemeState
import com.example.dicegame.data.AppState

@Composable
fun MainScreen(
    onNewGameClick: () -> Unit,
    appState: AppState
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showInstructionsDialog by remember { mutableStateOf(false) }
    val isDarkTheme = LocalThemeState.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme.value) Color(0xFF121212) else Color.White)
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
                onClick = { showInstructionsDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 8.dp)
            ) {
                Text("Instructions", fontSize = 18.sp)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dark Theme",
                fontSize = 16.sp,
                color = if (isDarkTheme.value) Color.White else Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Switch(
                checked = isDarkTheme.value,
                onCheckedChange = { isDarkTheme.value = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.size(width = 36.dp, height = 20.dp)
            )
        }
    }

    if (showInstructionsDialog) {
        AlertDialog(
            onDismissRequest = { showInstructionsDialog = false },
            title = { Text("How to Play", color = if (isDarkTheme.value) Color.White else Color.Black) },
            text = {
                Text(
                    "Welcome to Dice Game!\n\n" +
                    "Game Rules:\n" +
                    "1. Each player starts with 5 dice\n" +
                    "2. Players take turns rolling their dice\n" +
                    "3. After the first roll, you can:\n" +
                    "   - Select specific dice to reroll (up to 2 times)\n" +
                    "   - Keep your current roll and score\n" +
                    "4. The sum of your dice values is added to your total score\n" +
                    "5. The computer will also decide whether to reroll its dice\n" +
                    "6. First player to reach the target score wins!\n\n" +
                    "Tips:\n" +
                    "- Choose your rerolls wisely\n" +
                    "- Keep track of both scores\n" +
                    "- Plan your strategy based on the target score",
                    textAlign = TextAlign.Start,
                    color = if (isDarkTheme.value) Color.White else Color.Black
                )
            },
            confirmButton = {
                Button(onClick = { showInstructionsDialog = false }) {
                    Text("Got it!")
                }
            }
        )
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