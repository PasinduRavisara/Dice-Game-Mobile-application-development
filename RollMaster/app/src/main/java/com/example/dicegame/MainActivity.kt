package com.example.dicegame

// Import necessary Android and Compose components
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.dicegame.ui.screens.GameScreen
import com.example.dicegame.ui.screens.MainScreen
import com.example.dicegame.ui.theme.DiceGameTheme
import com.example.dicegame.ui.theme.rememberThemeState
import com.example.dicegame.ui.theme.LocalThemeState
import com.example.dicegame.data.AppState
import com.example.dicegame.data.GameState

// Main activity class for the dice game application
class MainActivity : ComponentActivity() {
    // Variables to hold saved state across configuration changes
    private var savedAppState: AppState? = null  // Saved app-wide state
    private var savedCurrentScreen: String = "main"  // Default to main screen
    private var savedGameState: GameState? = null  // Saved game state

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore saved state if available (after configuration changes)
        savedInstanceState?.let { bundle ->
            savedAppState = bundle.getParcelable("appState")  // Restore app state
            savedCurrentScreen = bundle.getString("currentScreen", "main")  // Restore screen
            savedGameState = bundle.getParcelable("gameState")  // Restore game state
        }

        // Set the Compose content for the activity
        setContent {
            // Remember and manage theme state (dark/light mode)
            val themeState = rememberThemeState()
            // Provide theme state to the entire composition
            CompositionLocalProvider(LocalThemeState provides themeState) {
                // Apply custom theme to all child composables
                DiceGameTheme {
                    // Main app composable with state management
                    DiceGameApp(
                        initialAppState = savedAppState,  // Pass restored app state
                        initialScreen = savedCurrentScreen,  // Pass restored screen
                        initialGameState = savedGameState,  // Pass restored game state
                        onScreenChange = { currentScreen ->
                            // Update saved screen when it changes
                            savedCurrentScreen = currentScreen
                        },
                        onGameStateChange = { gameState ->
                            // Update saved game state when it changes
                            savedGameState = gameState
                        }
                    )
                }
            }
        }
    }

    // Save current state before activity destruction (configuration changes)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current app state if available
        savedAppState?.let { state ->
            outState.putParcelable("appState", state)
        }
        // Save current screen
        outState.putString("currentScreen", savedCurrentScreen)
        // Save current game state if available
        savedGameState?.let { state ->
            outState.putParcelable("gameState", state)
        }
    }
}

// Main app composable that handles screen navigation and state management
@Composable
fun DiceGameApp(
    initialAppState: AppState?,  // Initial app state (can be restored)
    initialScreen: String,  // Initial screen to show
    initialGameState: GameState? = null,  // Initial game state (can be restored)
    onScreenChange: (String) -> Unit = {},  // Callback when screen changes
    onGameStateChange: (GameState?) -> Unit = {}  // Callback when game state changes
) {
    // State for app-wide data (wins count, etc.)
    var appState by rememberSaveable { mutableStateOf(initialAppState ?: AppState()) }
    // State for current screen (main or game)
    var currentScreen by rememberSaveable { mutableStateOf(initialScreen) }
    // State for current game data
    var gameState by rememberSaveable { mutableStateOf(initialGameState) }

    // Effect to notify parent when screen changes
    LaunchedEffect(currentScreen) {
        onScreenChange(currentScreen)
    }

    // Effect to notify parent when game state changes
    LaunchedEffect(gameState) {
        onGameStateChange(gameState)
    }

    // Screen navigation logic
    when (currentScreen) {
        "main" -> {
            // Show main menu screen
            MainScreen(
                onNewGameClick = { currentScreen = "game" },  // Navigate to game screen
                appState = appState  // Pass current app state
            )
        }
        "game" -> {
            // Show game screen
            GameScreen(
                onGameEnd = {
                    currentScreen = "main"
                    gameState = null // Reset game state when returning to main
                },  // Return to main menu
                appState = appState,  // Pass current app state
                onAppStateUpdate = { newAppState ->
                    appState = newAppState  // Update app state
                },
                initialGameState = gameState,  // Pass current game state
                onGameStateUpdate = { newGameState ->
                    gameState = newGameState  // Update game state
                }
            )
        }
    }
}