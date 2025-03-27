package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dicegame.ui.screens.GameScreen
import com.example.dicegame.ui.screens.MainScreen
import com.example.dicegame.ui.theme.DiceGameTheme
import com.example.dicegame.data.AppState

class MainActivity : ComponentActivity() {
    private var savedAppState: AppState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Restore saved state if available
        savedInstanceState?.let { bundle ->
            savedAppState = bundle.getParcelable("appState")
        }

        setContent {
            DiceGameTheme {
                DiceGameApp(savedAppState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current app state
        savedAppState?.let { state ->
            outState.putParcelable("appState", state)
        }
    }
}

@Composable
fun DiceGameApp(initialAppState: AppState?) {
    val navController = rememberNavController()
    var appState by remember { mutableStateOf(initialAppState ?: AppState()) }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onNewGameClick = { navController.navigate("game") },
                appState = appState
            )
        }
        composable("game") {
            GameScreen(
                onGameEnd = { navController.popBackStack() },
                appState = appState,
                onAppStateUpdate = { newAppState ->
                    appState = newAppState
                }
            )
        }
    }
}