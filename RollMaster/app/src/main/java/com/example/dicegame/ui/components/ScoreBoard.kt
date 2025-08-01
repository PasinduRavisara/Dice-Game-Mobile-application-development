// Package declaration for the DiceGame UI components
package com.example.dicegame.ui.components

// Import statements for necessary Compose and Android components
import androidx.compose.foundation.layout.*          // For layout-related components like Row, Column, etc.
import androidx.compose.material3.Text              // For displaying text
import androidx.compose.runtime.Composable          // For defining composable functions
import androidx.compose.ui.Alignment                // For alignment within rows/columns
import androidx.compose.ui.Modifier                 // For modifying the composables
import androidx.compose.ui.graphics.Color           // For color styling
import androidx.compose.ui.text.font.FontWeight     // For bold text styling
import androidx.compose.ui.unit.dp                  // For specifying dp units
import androidx.compose.ui.unit.sp                  // For specifying sp units (font size)

// Composable function to display the scoreboard
@Composable
fun ScoreBoard(
    humanScore: Int,                               // Human player's current score
    computerScore: Int,                            // Computer's current score
    currentAttempt: Int,                           // Current attempt number
    targetScore: Int,                              // Target score to reach
    isDarkTheme: Boolean                           // Flag for dark/light theme styling
) {
    // Row composable to layout the three sections (Human, Attempt, Computer) horizontally
    Row(
        modifier = Modifier
            .fillMaxWidth()                        // Row fills the full width of the screen
            .padding(8.dp),                        // Add 8dp padding around the row
        horizontalArrangement = Arrangement.SpaceBetween // Space the three sections evenly
    ) {
        // Column for displaying human player's score
        Column(horizontalAlignment = Alignment.CenterHorizontally) { // Center-align the content
            Text(
                "You",                             // Label for human player
                fontWeight = FontWeight.Bold,      // Bold font for label
                color = if (isDarkTheme) Color.White else Color.Black // Color based on theme
            )
            Text(
                // Kotlin string interpolation used here
                "$humanScore / $targetScore",      // Human's current score and target score
                fontSize = 18.sp,                  // Font size of 18sp
                color = if (isDarkTheme) Color.White else Color.Black // Color based on theme
            )
        }

        // Column for displaying current attempt number
        Column(horizontalAlignment = Alignment.CenterHorizontally) { // Center-align the content
            Text(
                "Attempt",                         // Label for current attempt
                fontWeight = FontWeight.Bold,      // Bold font for label
                color = if (isDarkTheme) Color.White else Color.Black // Color based on theme
            )
            Text(
                "$currentAttempt",                 // Display the current attempt number
                fontSize = 18.sp,                  // Font size of 18sp
                color = if (isDarkTheme) Color.White else Color.Black // Color based on theme
            )
        }

        // Column for displaying computer's score
        Column(horizontalAlignment = Alignment.CenterHorizontally) { // Center-align the content
            Text(
                "Computer",                        // Label for computer
                fontWeight = FontWeight.Bold,      // Bold font for label
                color = if (isDarkTheme) Color.White else Color.Black // Color based on theme
            )
            Text(
                "$computerScore / $targetScore",   // Computer's current score and target score
                fontSize = 18.sp,                  // Font size of 18sp
                color = if (isDarkTheme) Color.White else Color.Black // Color based on theme
            )
        }
    }
}
