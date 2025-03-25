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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGameTheme {
                DiceGameApp()
            }
        }
    }
}

@Composable
fun DiceGameApp() {
    val navController = rememberNavController()
    var appState by remember { mutableStateOf(AppState()) }

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