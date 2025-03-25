package com.example.dicegame.ui.components

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

@Composable
fun DiceView(
    die: Die,
    canSelect: Boolean = false,
    onClick: () -> Unit = {}
) {
    val diceImageResId = when (die.value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }

    Card(
        modifier = Modifier
            .size(60.dp)
            .padding(4.dp)
            .clickable(enabled = canSelect) { onClick() }
            .border(
                width = if (die.isSelected) 2.dp else 0.dp,
                color = if (die.isSelected) Color.Red else Color.Transparent
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = diceImageResId),
                contentDescription = "Dice with value ${die.value}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun DiceRow(
    dice: List<Die>,
    canSelect: Boolean = false,
    onDieClick: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        dice.forEachIndexed { index, die ->
            DiceView(
                die = die,
                canSelect = canSelect,
                onClick = { onDieClick(index) }
            )
        }
    }
}