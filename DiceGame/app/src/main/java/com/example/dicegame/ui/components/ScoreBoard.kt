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
    targetScore: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("You", fontWeight = FontWeight.Bold, color = Color.White)
            Text("$humanScore / $targetScore", fontSize = 18.sp, color = Color.White)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Attempt", fontWeight = FontWeight.Bold, color = Color.White)
            Text("$currentAttempt", fontSize = 18.sp, color = Color.White)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Computer", fontWeight = FontWeight.Bold, color = Color.White)
            Text("$computerScore / $targetScore", fontSize = 18.sp, color = Color.White)
        }
    }
}