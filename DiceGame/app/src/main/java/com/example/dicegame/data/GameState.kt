package com.example.dicegame.data

import android.os.Parcelable
import kotlin.random.Random
import kotlinx.parcelize.Parcelize

@Parcelize
data class Die(
    val value: Int = 1,
    val isSelected: Boolean = false
) : Parcelable

@Parcelize
data class PlayerState(
    val dice: List<Die> = List(5) { Die() },
    val currentScore: Int = 0,
    val totalScore: Int = 0,
    val rollCount: Int = 0,
    val rerollCount: Int = 0
) : Parcelable

@Parcelize
data class GameState(
    val humanPlayer: PlayerState = PlayerState(),
    val computerPlayer: PlayerState = PlayerState(),
    val targetScore: Int = 101,
    val gameEnded: Boolean = false,
    val winner: String = "",
    val isTieBreaker: Boolean = false,
    val attemptCount: Int = 0,
    val humanWins: Int = 0,
    val computerWins: Int = 0
) : Parcelable