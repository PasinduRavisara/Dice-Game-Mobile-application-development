package com.example.dicegame.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF7044FF),
    background = Color(0xFFF8F8F8),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFB00020),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF1565C0),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF7044FF),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

// Creates a CompositionLocal to provide theme state throughout the app
val LocalThemeState = compositionLocalOf { mutableStateOf(true) }  // Defaults to dark theme (true)

// Composable function that defines the app's theme
@Composable
fun DiceGameTheme(
    darkTheme: Boolean = LocalThemeState.current.value,  // Gets current theme from LocalThemeState
    content: @Composable () -> Unit  // Child composables to be themed
) {
    // Selects color scheme based on dark/light theme preference
    val colorScheme = if (darkTheme) DarkColors else LightColors

    // Applies the selected color scheme to MaterialTheme
    MaterialTheme(
        colorScheme = colorScheme,  // Uses the chosen color scheme
        content = content  // Applies theme to child content
    )
}

// Composable that remembers and saves theme state across configuration changes
@Composable
fun rememberThemeState(): MutableState<Boolean> {
    // Returns a remembered mutable state that survives configuration changes
    return rememberSaveable { mutableStateOf(true) }  // Defaults to dark theme (true)
}