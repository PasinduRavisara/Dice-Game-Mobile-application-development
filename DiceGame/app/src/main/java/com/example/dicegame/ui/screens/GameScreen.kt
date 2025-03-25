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
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun GameScreen(
    onGameEnd: () -> Unit
) {
    var gameState by remember { mutableStateOf(GameState()) }
    var showResultDialog by remember { mutableStateOf(false) }
    var targetScoreInput by remember { mutableStateOf("101") }
    var showTargetScoreDialog by remember { mutableStateOf(true) }
    var isComputerRerolling by remember { mutableStateOf(false) }
    var computerRerollCount by remember { mutableStateOf(0) }
    var isShowingFinalResult by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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

            if (isComputerRerolling) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Computer is rerolling... (${computerRerollCount}/2)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
            DiceRow(dice = gameState.computerPlayer.dice)
            }

            Text(
                "Current Roll: ${calculateDiceSum(gameState.computerPlayer.dice)} points",
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Game controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (!gameState.gameEnded && gameState.humanPlayer.rollCount > 0 && gameState.humanPlayer.rerollCount < 2) {
                        // Roll dice for human player
                        val updatedHumanDice = rollSelectedDice(gameState.humanPlayer.dice)

                        // Update game state with reroll
                        gameState = gameState.copy(
                            humanPlayer = gameState.humanPlayer.copy(
                                dice = updatedHumanDice,
                                rerollCount = gameState.humanPlayer.rerollCount + 1
                            )
                        )
                    }
                },
                enabled = !gameState.gameEnded && gameState.humanPlayer.rollCount == 1 && gameState.humanPlayer.rerollCount < 2,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 8.dp)
            ) {
                Text("Reroll (${2 - gameState.humanPlayer.rerollCount} left)")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (!gameState.gameEnded && gameState.humanPlayer.rollCount == 0) {
                            // Roll dice for human player
                            val updatedHumanDice = rollSelectedDice(gameState.humanPlayer.dice)

                            // First roll for computer
                            val computerDice = List(5) { Die(value = Random.nextInt(1, 7)) }

                            // Update game state
                                gameState = gameState.copy(
                                humanPlayer = gameState.humanPlayer.copy(
                                    dice = updatedHumanDice,
                                    rollCount = 1
                                ),
                                computerPlayer = gameState.computerPlayer.copy(
                                    dice = computerDice,
                                    rollCount = 1
                                )
                            )
                        }
                    },
                    enabled = !gameState.gameEnded && gameState.humanPlayer.rollCount == 0 && !isShowingFinalResult
            ) {
                Text("Throw")
            }

            Button(
                onClick = {
                    if (!gameState.gameEnded && gameState.humanPlayer.rollCount > 0) {
                            // Start computer's reroll process
                            isComputerRerolling = true
                            computerRerollCount = 0
                            
                            scope.launch {
                                var currentDice = gameState.computerPlayer.dice
                                
                                // First reroll attempt
                                if (Random.nextBoolean()) {
                                    delay(1000) // 1 second delay
                                    currentDice = computerStrategy(currentDice)
                                    computerRerollCount = 1
                                    gameState = gameState.copy(
                                        computerPlayer = gameState.computerPlayer.copy(
                                            dice = currentDice
                                        )
                                    )
                                }
                                
                                // Second reroll attempt
                                if (Random.nextBoolean()) {
                                    delay(1000) // 1 second delay
                                    currentDice = computerStrategy(currentDice)
                                    computerRerollCount = 2
                                    gameState = gameState.copy(
                                        computerPlayer = gameState.computerPlayer.copy(
                                            dice = currentDice
                                        )
                                    )
                                }
                                
                                // End rerolling state and proceed with scoring
                                isComputerRerolling = false
                                isShowingFinalResult = true
                                
                                // Update game state with computer's final dice before scoring
                                val stateWithComputerMoves = gameState.copy(
                                    computerPlayer = gameState.computerPlayer.copy(
                                        dice = currentDice
                                    )
                                )

                                handleScoring(stateWithComputerMoves) { updatedState ->
                                    // Update scores but keep the dice visible
                                    gameState = updatedState.copy(
                                        humanPlayer = updatedState.humanPlayer.copy(
                                            rollCount = 0,
                                            rerollCount = 0
                                        ),
                                        computerPlayer = updatedState.computerPlayer.copy(
                                            rollCount = 0
                                        )
                                    )
                            checkGameEnd(updatedState, { gameState = it }, { showResultDialog = it })
                                }

                                // Wait 3 seconds to show the final result
                                delay(3000)

                                // After 3 seconds, reset the dice for the next attempt
                                gameState = gameState.copy(
                                    humanPlayer = gameState.humanPlayer.copy(
                                        dice = List(5) { Die() }
                                    ),
                                    computerPlayer = gameState.computerPlayer.copy(
                                        dice = List(5) { Die() }
                                    )
                                )
                                isShowingFinalResult = false
                            }
                        }
                    },
                    enabled = !gameState.gameEnded && gameState.humanPlayer.rollCount == 1 && !isComputerRerolling
            ) {
                Text("Score")
                }
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
private fun computerStrategy(dice: List<Die>): List<Die> {
    // Randomly decide whether to reroll at all (50% chance)
    if (!Random.nextBoolean()) {
        return dice
    }

    // If rerolling, randomly select which dice to reroll
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