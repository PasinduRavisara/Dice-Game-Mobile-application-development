package com.example.dicegame.data

// Import necessary classes for Parcelable implementation
import android.os.Parcelable
import kotlin.random.Random
import kotlinx.parcelize.Parcelize

// Data class representing a single die with value and selection state
@Parcelize  // Enables Parcelable serialization
data class Die(
    val value: Int = 1,  // Current face value (1-6)
    val isSelected: Boolean = false  // Whether die is selected for reroll
) : Parcelable  // Allows passing between activities

// Data class representing player's current game state
@Parcelize
data class PlayerState(
    val dice: List<Die> = List(5) { Die() },  // List of 5 dice (default value 1)
    val currentScore: Int = 0,  // Score from current turn
    val totalScore: Int = 0,  // Cumulative game score
    val rollCount: Int = 0,  // Number of rolls this turn
    val rerollCount: Int = 0  // Number of rerolls used (max 2)
) : Parcelable

// Data class representing complete game state
@Parcelize
data class GameState(
    val humanPlayer: PlayerState = PlayerState(),  // Human player's state
    val computerPlayer: PlayerState = PlayerState(),  // Computer player's state
    val targetScore: Int = 101,  // Score needed to win (default 101)
    val gameEnded: Boolean = false,  // Whether game is over
    val winner: String = "",  // "human" or "computer" when game ends
    val isTieBreaker: Boolean = false,  // Flag for tie-breaker round
    val attemptCount: Int = 0,  // Number of turns taken
    val humanWins: Int = 0,  // Total human wins in session
    val computerWins: Int = 0  // Total computer wins in session
) : Parcelable