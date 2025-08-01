package com.example.dicegame.data

// Import necessary Android parcelization components
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Data class for storing app-wide persistent state
@Parcelize  // Enables automatic Parcelable implementation
data class AppState(
    val humanWins: Int = 0,  // Tracks total number of human player wins
    val computerWins: Int = 0  // Tracks total number of computer wins
) : Parcelable  // Allows state to be saved/restored across configuration changes