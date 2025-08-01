// Package declaration for the DiceGame UI components
package com.example.dicegame.ui.components

// Import statements for necessary Compose and Android components
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dicegame.R
import com.example.dicegame.data.Die

// Composable function to display a single dice
@Composable
fun DiceView(
    die: Die,                             // The Die object representing the dice
    canSelect: Boolean = false,           // Whether the dice can be clicked or not
    onClick: () -> Unit = {}              // Lambda for click behavior
) {
    // Determine the drawable resource ID based on the dice value
    val diceImageResId = when (die.value) {
        1 -> R.drawable.dice_1            // Image for dice with value 1
        2 -> R.drawable.dice_2            // Image for dice with value 2
        3 -> R.drawable.dice_3            // Image for dice with value 3
        4 -> R.drawable.dice_4            // Image for dice with value 4
        5 -> R.drawable.dice_5            // Image for dice with value 5
        6 -> R.drawable.dice_6            // Image for dice with value 6
        else -> R.drawable.dice_1         // Default to dice with value 1 if value is unexpected
    }

    // Card composable to display the dice with styling
    Card(
        modifier = Modifier
            .size(60.dp)                  // Set size of the card to 60dp x 60dp
            .padding(4.dp)                // Add 4dp padding around the card
            .clickable(enabled = canSelect) { onClick() } // Enable click only if canSelect is true
            .border(                      // Border around the dice
                width = if (die.isSelected) 2.dp else 0.dp, // 2dp red border if selected, no border otherwise
                color = if (die.isSelected) Color.Red else Color.Transparent
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Elevation for shadow effect
    ) {
        // Box to center the dice image within the card
        Box(
            contentAlignment = Alignment.Center, // Center the content
            modifier = Modifier.fillMaxSize()    // Box fills the card completely
        ) {
            // Display the dice image
            Image(
                painter = painterResource(id = diceImageResId), // Load the image based on dice value
                contentDescription = "Dice with value ${die.value}", // Accessibility description
                contentScale = ContentScale.Fit, // Scale the image to fit within the box
                modifier = Modifier
                    .fillMaxSize()          // Image fills the box completely
                    .padding(4.dp)          // Add 4dp padding within the image box
            )
        }
    }
}

// Composable function to display a row of dice
@Composable
fun DiceRow(
    dice: List<Die>,                       // List of Die objects to display
    canSelect: Boolean = false,            // Whether the dice can be clicked or not
    onDieClick: (Int) -> Unit = {}         // Lambda to handle dice click, receives index of clicked dice
) {
    // Row composable to arrange dice horizontally
    Row(
        modifier = Modifier
            .fillMaxWidth()                // Row fills the full width of the screen
            .padding(8.dp),                // Add 8dp padding around the row
        horizontalArrangement = Arrangement.Center // Center the dice in the row
    ) {
        // For each dice in the list, create a DiceView
        dice.forEachIndexed { index, die -> // Use index for click handling
            DiceView(
                die = die,                  // Pass the current die object
                canSelect = canSelect,      // Whether the dice can be clicked
                onClick = { onDieClick(index) } // Pass index of clicked dice to the onDieClick lambda
            )
        }
    }
}
