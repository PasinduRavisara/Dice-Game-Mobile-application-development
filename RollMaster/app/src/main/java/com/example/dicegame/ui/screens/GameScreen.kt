// Package declaration for the GameScreen composable
package com.example.dicegame.ui.screens

// Import necessary Android and Compose libraries
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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
import com.example.dicegame.data.Die
import com.example.dicegame.data.GameState
import com.example.dicegame.data.PlayerState
import com.example.dicegame.ui.components.DiceRow
import com.example.dicegame.ui.components.ScoreBoard
import com.example.dicegame.ui.theme.LocalThemeState
import kotlin.random.Random
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.runtime.rememberCoroutineScope
import androidx.activity.compose.BackHandler
import com.example.dicegame.data.AppState

// Main GameScreen composable function
@Composable
fun GameScreen(
    onGameEnd: () -> Unit, // what to do when exiting the game
    appState: AppState, // tracks wins/losses.
    onAppStateUpdate: (AppState) -> Unit, // Callback to update app state
    initialGameState: GameState? = null, // loads saved state on rotation.
    onGameStateUpdate: (GameState) -> Unit = {} // Callback to update game state
) {
    // A state variable to initialize or restore the game state.
    var gameState by rememberSaveable {  // rememberSaveable allows state to survive configuration changes
        mutableStateOf(  // creates a mutable state holder.
            initialGameState ?: GameState( // Use initial state if provided, otherwise create new
                humanWins = appState.humanWins,
                computerWins = appState.computerWins,
                attemptCount = 1  // First round starts with attempt number 1 instead of 0
            )
        )
    }

    // State for showing result dialog
    var showResultDialog by rememberSaveable { mutableStateOf(false) }

    // State for target score input with default value 101
    var targetScoreInput by rememberSaveable {
        mutableStateOf(initialGameState?.targetScore?.toString() ?: "101")
    }

    // State for showing target score dialog (shown if no target score set)
    var showTargetScoreDialog by rememberSaveable {
        mutableStateOf(initialGameState?.targetScore == null || initialGameState?.targetScore == 101)
    }

    // State for computer reroll animation
    var isComputerRerolling by rememberSaveable { mutableStateOf(false) }

    // Counter for computer reroll steps
    var computerRerollCount by rememberSaveable { mutableStateOf(0) }

    // State for showing final result before reset
    var isShowingFinalResult by rememberSaveable { mutableStateOf(false) }

    // Coroutine scope for operations like delays
    val scope = rememberCoroutineScope()    //use rememberCoroutineScope Because in a Composable, cannot launch coroutines directly.

    // Current theme state
    val isDarkTheme = LocalThemeState.current

    // Handle back button press
    BackHandler {
        when {
            showResultDialog -> {
                // If result dialog is shown, dismiss it first
                showResultDialog = false
            }
            showTargetScoreDialog -> {
                // If target score dialog is shown, dismiss it and go back to main menu
                showTargetScoreDialog = false
                onGameEnd()
            }
            else -> {
                // Otherwise, go back to main menu
                onGameEnd()
            }
        }
    }

    // Save game state to app state whenever it changes
    LaunchedEffect(gameState) {
        onAppStateUpdate(AppState(
            humanWins = gameState.humanWins,
            computerWins = gameState.computerWins
        ))
        // Also save the current game state
        onGameStateUpdate(gameState)
    }

    // Target score dialog
    if (showTargetScoreDialog) {
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss without selecting */ },
            title = { Text("Set Target Score") }, // Dialog title
            text = {
                OutlinedTextField(
                    value = targetScoreInput,
                    onValueChange = {
                        // Accept only numbers
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            targetScoreInput = it
                        }
                    },
                    label = { Text("Target Score") } // Input label
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Parse input and start game
                        val targetScore = targetScoreInput.toIntOrNull() ?: 101
                        gameState = gameState.copy(targetScore = targetScore)
                        showTargetScoreDialog = false
                    }
                ) {
                    Text("Start Game") // Confirm button text
                }
            }
        )
    }

    // Main game UI
    Column(
        modifier = Modifier
            .fillMaxSize() // Take up all available space
            .background(if (isDarkTheme.value) Color(0xFF121212) else Color.White) // Theme-based background
            .padding(16.dp), // Add padding
        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
    ) {
        // Wins display on top left
        Box(
            modifier = Modifier.fillMaxWidth() // Take full width
        ) {
            Text(
                "H:${appState.humanWins}/C:${appState.computerWins}", // Win count display
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme.value) Color.White else Color.Black,
                modifier = Modifier.align(Alignment.TopStart) // Align to top start
            )
        }

        Spacer(modifier = Modifier.height(8.dp)) // Add vertical space

        // Score display component
        ScoreBoard(
            humanScore = gameState.humanPlayer.totalScore,
            computerScore = gameState.computerPlayer.totalScore,
            currentAttempt = gameState.attemptCount,
            targetScore = gameState.targetScore,
            isDarkTheme = isDarkTheme.value
        )

        Spacer(modifier = Modifier.height(16.dp)) // Add more vertical space

        // Player dice section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Your Dice", // Section title
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isDarkTheme.value) Color.White else Color.Black
            )

            // Dice row component for player's dice
            DiceRow(
                dice = gameState.humanPlayer.dice, // Pass current human player's dice list
                canSelect = gameState.humanPlayer.rollCount > 0 && gameState.humanPlayer.rerollCount < 2 && !gameState.gameEnded,
                // Allow dice selection only if player has rolled, can still reroll, and game not ended
                onDieClick = { index -> // When a die is clicked
                    // Toggle selection of dice when clicked
                    if (gameState.humanPlayer.rollCount > 0 && gameState.humanPlayer.rerollCount < 2) {
                        // Update game state by modifying selected dice
                        gameState = gameState.copy(
                            humanPlayer = gameState.humanPlayer.copy( // Create updated copy of human player
                                dice = gameState.humanPlayer.dice.mapIndexed { i, die -> // Iterate through dice
                                    if (i == index) die.copy(isSelected = !die.isSelected) else die // Toggle isSelected for clicked die
                                }
                            )
                        )
                    }
                }
            )

            // Current roll points display
            Text(
                "Current Roll: ${calculateDiceSum(gameState.humanPlayer.dice)} points",
                modifier = Modifier.padding(top = 4.dp),
                color = if (isDarkTheme.value) Color.White else Color.Black
            )

            // Show selection status if reroll is allowed
            if (gameState.humanPlayer.rollCount > 0 && gameState.humanPlayer.rerollCount < 2) {
                val selectedCount = gameState.humanPlayer.dice.count { it.isSelected } // Count selected dice
                Text(
                    "Selected: $selectedCount dice to keep", // Show how many dice are selected
                    fontSize = 12.sp,
                    color = if (isDarkTheme.value) Color.Gray else Color.DarkGray, // Subtle color for info text
                    modifier = Modifier.padding(top = 2.dp) // Add small top padding
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // Add spacing between sections

        // Computer dice section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Computer's Dice", // Section title
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isDarkTheme.value) Color.White else Color.Black
            )

            // Show loading animation when computer is rerolling
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
                        CircularProgressIndicator() // Loading spinner
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Computer is rerolling... (${computerRerollCount}/2)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme.value) Color.White else Color.Black
                        )
                    }
                }
            } else {
                // Show computer's dice when not rerolling
                DiceRow(dice = gameState.computerPlayer.dice)
            }

            // Computer's current roll points
            Text(
                "Current Roll: ${calculateDiceSum(gameState.computerPlayer.dice)} points",
                modifier = Modifier.padding(top = 4.dp),
                color = if (isDarkTheme.value) Color.White else Color.Black
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Push controls to bottom

        // Game controls section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dynamic instructions based on game state
            if (gameState.humanPlayer.rollCount == 0) {
                // No roll yet
                Text(
                    "Press Throw to start rolling your dice", // Guide user to press Throw
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = if (isDarkTheme.value) Color.White else Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else if (gameState.humanPlayer.rerollCount < 2) {
                // Player can still reroll
                Text(
                    "Select dice to keep, then press Reroll to reroll the rest", // Show reroll instructions
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = if (isDarkTheme.value) Color.White else Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                // Reroll limit reached
                Text(
                    "Press Score to end your turn and let the computer play", // Prompt to proceed to computer turn
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = if (isDarkTheme.value) Color.White else Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Control buttons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly // Evenly space buttons
            ) {
                // Throw Button - initial roll
                Button(
                    onClick = {
                        if (!gameState.gameEnded && gameState.humanPlayer.rollCount == 0 && !isShowingFinalResult) {
                            // Generate random dice for player
                            val updatedHumanDice = List(5) { Die(value = Random.nextInt(1, 7)) }

                            // Generate random dice for computer
                            val computerDice = List(5) { Die(value = Random.nextInt(1, 7)) }

                            // Update game state with new dice and roll counts
                            gameState = gameState.copy(
                                humanPlayer = gameState.humanPlayer.copy(
                                    dice = updatedHumanDice,
                                    rollCount = 1 // Mark that human rolled
                                ),
                                computerPlayer = gameState.computerPlayer.copy(
                                    dice = computerDice,
                                    rollCount = 1 // Mark that computer rolled
                                )
                            )
                        }
                    },
                    enabled = !gameState.gameEnded &&
                            gameState.humanPlayer.rollCount == 0 &&
                            !isShowingFinalResult
                ) {
                    Text("Throw") // Button text
                }

                // Reroll Button - reroll unselected dice
                Button(
                    onClick = {
                        if (!gameState.gameEnded && gameState.humanPlayer.rollCount > 0 && gameState.humanPlayer.rerollCount < 2 && !isShowingFinalResult) {
                            // Reroll only unselected dice
                            val updatedHumanDice = rollUnselectedDice(gameState.humanPlayer.dice)

                            // Update game state with reroll
                            gameState = gameState.copy(
                                humanPlayer = gameState.humanPlayer.copy(
                                    dice = updatedHumanDice,
                                    rerollCount = gameState.humanPlayer.rerollCount + 1
                                )
                            )
                        }
                    },
                    enabled = !gameState.gameEnded &&
                            gameState.humanPlayer.rollCount > 0 &&
                            gameState.humanPlayer.rerollCount < 2 &&
                            !isShowingFinalResult
                ) {
                    Text("Reroll (${2 - gameState.humanPlayer.rerollCount} left)") // Show remaining rerolls
                }

                // Score Button - end turn
                Button(
                    onClick = {
                        if (!gameState.gameEnded && gameState.humanPlayer.rollCount > 0) {
                            // Start computer's turn animation
                            isComputerRerolling = true
                            computerRerollCount = 0

                            // Launch coroutine for computer's turn
                            scope.launch {
                                var currentDice = gameState.computerPlayer.dice // Get current dice of computer

                                // First reroll (50% chance)
                                if (Random.nextBoolean()) { // Random decision to reroll or not
                                    delay(1000) // Add 1 second delay for animation effect
                                    currentDice = computerStrategy(currentDice) // Apply computer's strategy to reroll dice
                                    computerRerollCount = 1 // Update reroll count
                                    gameState = gameState.copy(
                                        computerPlayer = gameState.computerPlayer.copy(
                                            dice = currentDice // Update computer dice in game state
                                        )
                                    )
                                }

                                // Second reroll (another 50% chance)
                                if (Random.nextBoolean()) {
                                    delay(1000) // Delay again for animation
                                    currentDice = computerStrategy(currentDice) // Apply strategy again
                                    computerRerollCount = 2 // Update reroll count to 2
                                    gameState = gameState.copy(
                                        computerPlayer = gameState.computerPlayer.copy(
                                            dice = currentDice // Update dice in game state
                                        )
                                    )
                                }

                                // End rerolling visual state
                                isComputerRerolling = false
                                isShowingFinalResult = true // Start showing results to user

                                // Prepare new state with final dice values for scoring
                                val stateWithComputerMoves = gameState.copy(
                                    computerPlayer = gameState.computerPlayer.copy(
                                        dice = currentDice
                                    )
                                )

                                // Handle scoring logic
                                handleScoring(stateWithComputerMoves) { updatedState ->
                                    // Update game state with new scores and reset for next round
                                    gameState = updatedState.copy(
                                        humanPlayer = updatedState.humanPlayer.copy(
                                            rollCount = 0, // Reset roll count for human
                                            rerollCount = 0 // Reset reroll count for human
                                        ),
                                        computerPlayer = updatedState.computerPlayer.copy(
                                            rollCount = 0 // Reset computer's roll count
                                        )
                                    )
                                    // Check if the game has ended, and update accordingly
                                    checkGameEnd(
                                        updatedState,
                                        { gameState = it }, // Update game state if ended
                                        { showResultDialog = it }, // Show result dialog if game ends
                                        onAppStateUpdate // Notify app state change
                                    )
                                }

                                // Display result briefly for 2 seconds
                                delay(2000)

                                // Reset both players' dice for the next round
                                gameState = gameState.copy(
                                    humanPlayer = gameState.humanPlayer.copy(
                                        dice = List(5) { Die() } // Reset to fresh dice
                                    ),
                                    computerPlayer = gameState.computerPlayer.copy(
                                        dice = List(5) { Die() } // Reset to fresh dice
                                    )
                                )

                                isShowingFinalResult = false // Hide result view, ready for next turn
                            }

                        }
                    },
                    enabled = !gameState.gameEnded &&
                            gameState.humanPlayer.rollCount > 0 &&
                            !isComputerRerolling
                ) {
                    Text("Score") // Button text
                }
            }
        }
    }

    // Winner dialog - shown when game ends
    if (showResultDialog) {
        val isHumanWinner = gameState.winner == "human" //Checks if the human player won the game. True or false.
        AlertDialog(
            onDismissRequest = { onGameEnd() }, // returns the user to the Main screen.
            title = {
                Text(
                    if (isHumanWinner) "You Win!" else "You Lose", // Title based on winner
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    if (isHumanWinner)
                        "Congratulations! You reached ${gameState.humanPlayer.totalScore} points in ${gameState.attemptCount} attempts."
                    else
                        "The computer reached ${gameState.computerPlayer.totalScore} points in ${gameState.attemptCount} attempts.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
                ) {
                    // New Game button
                    Button(
                        onClick = {
                            // Reset game state
                            gameState = GameState(
                                humanWins = gameState.humanWins,
                                computerWins = gameState.computerWins,
                                attemptCount = 1
                            )
                            showResultDialog = false
                            showTargetScoreDialog = true
                            isComputerRerolling = false
                            computerRerollCount = 0
                            isShowingFinalResult = false
                            targetScoreInput = "101"
                        },
                        modifier = Modifier.weight(1f) // Equal weight
                    ) {
                        Text("New Game")
                    }
                    // Main Menu button
                    Button(
                        onClick = onGameEnd,
                        modifier = Modifier.weight(1f) // Equal weight
                    ) {
                        Text("Main Menu")
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = if (isHumanWinner) Color.Green else Color.Red // Color based on winner
        )
    }
}

// Helper function to calculate sum of dice values
private fun calculateDiceSum(dice: List<Die>): Int {
    return dice.sumOf { it.value } // Sum all dice values
}

// Helper function to roll only the unselected dice
private fun rollUnselectedDice(dice: List<Die>): List<Die> {
    return dice.map { die ->
        if (die.isSelected) {
            // Keep selected dice but clear selection
            die.copy(isSelected = false)
        } else {
            // Reroll unselected dice
            Die(value = Random.nextInt(1, 7), isSelected = false)
        }
    }
}

// Computer's strategy for rerolls (randomized logic)
private fun computerStrategy(dice: List<Die>): List<Die> {
    // 50% chance that the computer does not reroll at all
    if (!Random.nextBoolean()) {
        return dice // Return original dice without changes
    }

    // If rerolling, decide for each die whether to keep or reroll
    return dice.map { die ->
        if (Random.nextBoolean()) {
            // 50% chance to reroll this specific die
            Die(value = Random.nextInt(1, 7)) // Generate a new random value between 1 and 6
        } else {
            die // Keep the current die as it is
        }
    }
}


// Helper function to handle scoring logic for both players
private fun handleScoring(
    currentState: GameState, // Current state of the game before scoring
    onStateUpdate: (GameState) -> Unit // Callback function to update the state
) {
    // Calculate the sum of dice values for the human player
    val humanSum = calculateDiceSum(currentState.humanPlayer.dice)

    // Calculate the sum of dice values for the computer player
    val computerSum = calculateDiceSum(currentState.computerPlayer.dice)

    // Create a new updated game state with updated scores
    val updatedState = currentState.copy(
        humanPlayer = currentState.humanPlayer.copy(
            totalScore = currentState.humanPlayer.totalScore + humanSum // Add this round's points to human's total
        ),
        computerPlayer = currentState.computerPlayer.copy(
            totalScore = currentState.computerPlayer.totalScore + computerSum // Add this round's points to computer's total
        ),
        attemptCount = currentState.attemptCount + 1 // Increase the number of turns/attempts played
    )

    // Call the callback function with the updated state to apply the changes
    onStateUpdate(updatedState)
}


// Helper function to check if the game has ended
private fun checkGameEnd(
    currentState: GameState, // The current state of the game
    onStateUpdate: (GameState) -> Unit, // Callback to update game state
    onShowResultDialog: (Boolean) -> Unit, // Callback to show or hide the result dialog
    onAppStateUpdate: (AppState) -> Unit // Callback to update overall app state (win counts)
) {
    // Get current total scores for both players
    val humanScore = currentState.humanPlayer.totalScore
    val computerScore = currentState.computerPlayer.totalScore
    val targetScore = currentState.targetScore // Score required to win

    // Check if either player has reached or passed the target score
    if (humanScore >= targetScore || computerScore >= targetScore) {
        if (humanScore == computerScore) {
            // If scores are equal, it's a tie → enter tie-breaker mode
            onStateUpdate(currentState.copy(
                isTieBreaker = true, // Flag that we're in a tie-breaker
                humanPlayer = currentState.humanPlayer.copy(rollCount = 0), // Reset roll count
                computerPlayer = currentState.computerPlayer.copy(rollCount = 0) // Reset roll count
            ))
        } else {
            // Game has a clear winner (no tie)
            val isHumanWinner = humanScore > computerScore // Determine winner

            // Update win counters
            val newHumanWins = if (isHumanWinner) currentState.humanWins + 1 else currentState.humanWins
            val newComputerWins = if (!isHumanWinner) currentState.computerWins + 1 else currentState.computerWins

            // Update the game state with final outcome
            onStateUpdate(currentState.copy(
                gameEnded = true, // Mark game as finished
                winner = if (isHumanWinner) "human" else "computer", // Set winner string
                humanWins = newHumanWins, // Update human win count
                computerWins = newComputerWins // Update computer win count
            ))

            // Also update the global app state
            onAppStateUpdate(AppState(humanWins = newHumanWins, computerWins = newComputerWins))

            // Show the final result dialog to the user
            onShowResultDialog(true)
        }
    }
}
