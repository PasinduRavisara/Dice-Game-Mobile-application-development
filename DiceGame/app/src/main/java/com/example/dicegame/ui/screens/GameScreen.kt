package com.example.dicegame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegame.data.Die
import com.example.dicegame.data.GameState
import com.example.dicegame.data.PlayerState
import com.example.dicegame.ui.components.DiceRow
import com.example.dicegame.ui.components.ScoreBoard
import kotlin.random.Random

@Composable
fun GameScreen(
    onGameEnd: () -> Unit
) {
    var gameState by remember { mutableStateOf(GameState()) }
    var showResultDialog by remember { mutableStateOf(false) }
    var targetScoreInput by remember { mutableStateOf("101") }
    var showTargetScoreDialog by remember { mutableStateOf(true) }

    // Target score dialog
    if (showTargetScoreDialog) {
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss without selecting */ },
            title = { Text("Set Target Score") },
            text = {
                OutlinedTextField(
                    value = targetScoreInput,
                    onValueChange = {
                        // Accept only numbers
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            targetScoreInput = it
                        }
                    },
                    label = { Text("Target Score") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetScore = targetScoreInput.toIntOrNull() ?: 101
                        gameState = gameState.copy(targetScore = targetScore)
                        showTargetScoreDialog = false
                    }
                ) {
                    Text("Start Game")
                }
            }
        )
    }

    // Main game UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Score display
        ScoreBoard(
            humanScore = gameState.humanPlayer.totalScore,
            computerScore = gameState.computerPlayer.totalScore,
            currentAttempt = gameState.attemptCount,
            targetScore = gameState.targetScore
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Player dice section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Your Dice",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            DiceRow(
                dice = gameState.humanPlayer.dice,
                canSelect = gameState.humanPlayer.rollCount < 3 && !gameState.gameEnded,
                onDieClick = { index ->
                    if (gameState.humanPlayer.rollCount > 0 && gameState.humanPlayer.rollCount < 3) {
                        gameState = gameState.copy(
                            humanPlayer = gameState.humanPlayer.copy(
                                dice = gameState.humanPlayer.dice.mapIndexed { i, die ->
                                    if (i == index) die.copy(isSelected = !die.isSelected) else die
                                }
                            )
                        )
                    }
                }
            )

            Text(
                "Current Roll: ${calculateDiceSum(gameState.humanPlayer.dice)} points",
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Computer dice section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Computer's Dice",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            DiceRow(dice = gameState.computerPlayer.dice)

            Text(
                "Current Roll: ${calculateDiceSum(gameState.computerPlayer.dice)} points",
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Game controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (!gameState.gameEnded && gameState.humanPlayer.rollCount < 3) {
                        // Roll dice for human player
                        val updatedHumanDice = rollSelectedDice(gameState.humanPlayer.dice)

                        // First roll for computer or tie breaker
                        val computerDice = if (gameState.computerPlayer.rollCount == 0 || gameState.isTieBreaker) {
                            List(5) { Die() }  // New random dice for computer
                        } else {
                            gameState.computerPlayer.dice
                        }

                        // Update game state
                        gameState = gameState.copy(
                            humanPlayer = gameState.humanPlayer.copy(
                                dice = updatedHumanDice,
                                rollCount = gameState.humanPlayer.rollCount + 1
                            ),
                            computerPlayer = gameState.computerPlayer.copy(
                                dice = computerDice,
                                rollCount = if (gameState.computerPlayer.rollCount == 0 || gameState.isTieBreaker)
                                    gameState.computerPlayer.rollCount + 1
                                else
                                    gameState.computerPlayer.rollCount
                            )
                        )

                        // Handle auto-scoring after 3rd roll
                        if (gameState.humanPlayer.rollCount == 3) {
                            handleScoring(gameState) { updatedState ->
                                gameState = updatedState
                                checkGameEnd(updatedState, { gameState = it }, { showResultDialog = it })
                            }
                        }

                        // Handle tie breaker
                        if (gameState.isTieBreaker) {
                            val humanSum = calculateDiceSum(gameState.humanPlayer.dice)
                            val computerSum = calculateDiceSum(gameState.computerPlayer.dice)

                            if (humanSum != computerSum) {
                                gameState = gameState.copy(
                                    gameEnded = true,
                                    winner = if (humanSum > computerSum) "human" else "computer"
                                )
                                showResultDialog = true
                            }
                        }
                    }
                },
                enabled = !gameState.gameEnded && gameState.humanPlayer.rollCount < 3
            ) {
                Text("Throw")
            }

            Button(
                onClick = {
                    if (!gameState.gameEnded && gameState.humanPlayer.rollCount > 0) {
                        handleScoring(gameState) { updatedState ->
                            gameState = updatedState
                            checkGameEnd(updatedState, { gameState = it }, { showResultDialog = it })
                        }
                    }
                },
                enabled = !gameState.gameEnded && gameState.humanPlayer.rollCount > 0 && gameState.humanPlayer.rollCount < 3
            ) {
                Text("Score")
            }
        }
    }

    // Winner dialog
    if (showResultDialog) {
        val isHumanWinner = gameState.winner == "human"
        AlertDialog(
            onDismissRequest = { onGameEnd() },
            title = { Text(if (isHumanWinner) "You Win!" else "You Lose") },
            text = {
                Text(
                    if (isHumanWinner)
                        "Congratulations! You reached ${gameState.humanPlayer.totalScore} points in ${gameState.attemptCount} attempts."
                    else
                        "The computer reached ${gameState.computerPlayer.totalScore} points in ${gameState.attemptCount} attempts.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(onClick = onGameEnd) {
                    Text("Back to Main Menu")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = if (isHumanWinner) Color.Green else Color.Red
        )
    }
}

// Helper function to calculate sum of dice values
private fun calculateDiceSum(dice: List<Die>): Int {
    return dice.sumOf { it.value }
}

// Helper function to roll only the selected dice (or all dice if none selected)
private fun rollSelectedDice(dice: List<Die>): List<Die> {
    // If no dice are selected and this isn't the first roll, select all dice
    val anySelected = dice.any { it.isSelected }

    return dice.map { die ->
        if (die.isSelected || !anySelected) {
            Die(value = Random.nextInt(1, 7), isSelected = false)
        } else {
            die
        }
    }
}

// Implement computer's random strategy for rerolls
private fun computerStrategy(dice: List<Die>, currentRoll: Int): List<Die> {
    // Decide if computer wants to reroll (50% chance)
    val wantsToReroll = Random.nextBoolean()

    if (!wantsToReroll || currentRoll >= 3) {
        return dice
    }

    // Randomly select dice to reroll
    return dice.map { die ->
        if (Random.nextBoolean()) {
            Die(value = Random.nextInt(1, 7))
        } else {
            die
        }
    }
}

// Helper function to handle scoring logic
private fun handleScoring(
    currentState: GameState,
    onStateUpdate: (GameState) -> Unit
) {
    val humanSum = calculateDiceSum(currentState.humanPlayer.dice)
    val computerSum = calculateDiceSum(currentState.computerPlayer.dice)

    val updatedState = currentState.copy(
        humanPlayer = currentState.humanPlayer.copy(
            totalScore = currentState.humanPlayer.totalScore + humanSum
        ),
        computerPlayer = currentState.computerPlayer.copy(
            totalScore = currentState.computerPlayer.totalScore + computerSum
        ),
        attemptCount = currentState.attemptCount + 1
    )

    onStateUpdate(updatedState)
}

// Helper function to check if the game has ended
private fun checkGameEnd(
    currentState: GameState,
    onStateUpdate: (GameState) -> Unit,
    onShowResultDialog: (Boolean) -> Unit
) {
    val humanScore = currentState.humanPlayer.totalScore
    val computerScore = currentState.computerPlayer.totalScore
    val targetScore = currentState.targetScore

    if (humanScore >= targetScore || computerScore >= targetScore) {
        if (humanScore == computerScore) {
            // Tie breaker needed
            onStateUpdate(currentState.copy(
                isTieBreaker = true,
                humanPlayer = currentState.humanPlayer.copy(rollCount = 0),
                computerPlayer = currentState.computerPlayer.copy(rollCount = 0)
            ))
        } else {
            // Game ended with a winner
            onStateUpdate(currentState.copy(
                gameEnded = true,
                winner = if (humanScore > computerScore) "human" else "computer"
            ))
            onShowResultDialog(true)
        }
    }
}