package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dicegame.ui.screens.GameScreen
import com.example.dicegame.ui.screens.MainScreen
import com.example.dicegame.ui.theme.DiceGameTheme

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

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onNewGameClick = { navController.navigate("game") },
            )
        }
        composable("game") {
            GameScreen(
                onGameEnd = { navController.popBackStack() }
            )
        }
    }
}