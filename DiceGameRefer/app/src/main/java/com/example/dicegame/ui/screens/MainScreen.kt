package com.example.dicegame.ui.screens

// Import necessary Compose and Material3 components
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegame.ui.theme.LocalThemeState
import com.example.dicegame.data.AppState

// MainScreen composable - The entry point of the application
@Composable
fun MainScreen(
    onNewGameClick: () -> Unit, // Callback for when New Game button is clicked
    appState: AppState // Current application state
) {
    // State for showing/hiding the About dialog
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }

    // State for showing/hiding the Instructions dialog
    var showInstructionsDialog by rememberSaveable { mutableStateOf(false) }

    // Current theme state (dark/light)
    val isDarkTheme = LocalThemeState.current

    // Main column layout that fills the entire screen
    Column(
        modifier = Modifier
            .fillMaxSize() // Take up all available space
            .background(if (isDarkTheme.value) Color(0xFF121212) else Color.White) // Theme-based background
            .padding(16.dp), // Add padding around edges
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
        verticalArrangement = Arrangement.SpaceBetween // Space out content vertically
    ) {
        // Content column (centered vertically)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f), // Take up available space
            verticalArrangement = Arrangement.Center // Center content vertically
        ) {
            // Application title
            Text(
                text = "RollMaster", // Game title
                fontSize = 32.sp, // Large font size
                modifier = Modifier.padding(bottom = 32.dp), // Add space below
                color = if (isDarkTheme.value) Color.White else Color.Black // Theme-based text color
            )

            // New Game button
            Button(
                onClick = onNewGameClick, // Launch new game when clicked
                modifier = Modifier
                    .fillMaxWidth(0.8f) // 80% of screen width
                    .padding(vertical = 8.dp) // Add vertical padding
            ) {
                Text("New Game", fontSize = 18.sp) // Button text
            }

            // Instructions button
            Button(
                onClick = { showInstructionsDialog = true }, // Show instructions dialog
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 8.dp)
            ) {
                Text("Instructions", fontSize = 18.sp)
            }

            // About button
            Button(
                onClick = { showAboutDialog = true }, // Show about dialog
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 8.dp)
            ) {
                Text("About", fontSize = 18.sp)
            }
        }

        // Bottom section containing theme toggle
        Column(
            modifier = Modifier
                .fillMaxWidth() // Full width
                .padding(bottom = 16.dp), // Padding at bottom
            horizontalAlignment = Alignment.CenterHorizontally // Center content
        ) {
            // Theme toggle label
            Text(
                text = "Dark Theme",
                fontSize = 16.sp,
                color = if (isDarkTheme.value) Color.White else Color.Black,
                modifier = Modifier.padding(bottom = 8.dp) // Space below label
            )

            // Theme toggle switch
            Switch(
                checked = isDarkTheme.value, // Current theme state
                onCheckedChange = { isDarkTheme.value = it }, // Toggle theme
                colors = SwitchDefaults.colors( // Custom colors for the switch
                    checkedThumbColor = Color(0xFFD32F2F), // Red thumb when dark
                    checkedTrackColor = Color(0xDD070101), // Dark red track
                    uncheckedThumbColor = MaterialTheme.colorScheme.surface, // Light thumb
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant // Light track
                ),
                modifier = Modifier.size(width = 36.dp, height = 20.dp) // Switch size
            )
        }
    }

    // Instructions Dialog - shown when showInstructionsDialog is true
    if (showInstructionsDialog) {
        AlertDialog(
            onDismissRequest = { showInstructionsDialog = false }, // Close when clicked outside
            title = {
                Text("How to Play",
                    color = if (isDarkTheme.value) Color.White else Color.Black)
            },
            text = {
                Text(
                    "Welcome to RollMaster!\n\n" +
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
                    textAlign = TextAlign.Start, // Left-aligned text
                    color = if (isDarkTheme.value) Color.White else Color.Black // Theme text color
                )
            },
            confirmButton = {
                Button(onClick = { showInstructionsDialog = false }) { // Close dialog
                    Text("Got it!")
                }
            }
        )
    }

    // About Dialog - shown when showAboutDialog is true
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false }, // Close when clicked outside
            title = {
                Text("About",
                    color = if (isDarkTheme.value) Color.White else Color.Black)
            },
            text = {
                Text(
                    "Student ID: 20527507\nName: Pasindu Ravisara\n\n" +
                            "I confirm that I understand what plagiarism is and have read and understood " +
                            "the section on assessment offenses in the Essential Information for Students. " +
                            "The work that I have submitted is entirely my own. Any work from other authors " +
                            "is duly referenced and acknowledged.",
                    textAlign = TextAlign.Justify, // Justified text alignment
                    color = if (isDarkTheme.value) Color.White else Color.Black // Theme text color
                )
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) { // Close dialog
                    Text("OK")
                }
            }
        )
    }
}