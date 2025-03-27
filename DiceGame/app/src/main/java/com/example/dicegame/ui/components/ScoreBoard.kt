package com.example.dicegame.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreBoard(
    humanScore: Int,
    computerScore: Int,
    currentAttempt: Int,
    targetScore: Int,
    isDarkTheme: Boolean,
    humanWins: Int,
    computerWins: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("You", fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color.White else Color.Black)
            Text("$humanScore / $targetScore", fontSize = 18.sp, color = if (isDarkTheme) Color.White else Color.Black)
            Text("Wins: $humanWins", fontSize = 14.sp, color = if (isDarkTheme) Color.White else Color.Black)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Attempt", fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color.White else Color.Black)
            Text("$currentAttempt", fontSize = 18.sp, color = if (isDarkTheme) Color.White else Color.Black)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Computer", fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color.White else Color.Black)
            Text("$computerScore / $targetScore", fontSize = 18.sp, color = if (isDarkTheme) Color.White else Color.Black)
            Text("Wins: $computerWins", fontSize = 14.sp, color = if (isDarkTheme) Color.White else Color.Black)
        }
    }
}