package com.example.dicegame.data

import kotlin.random.Random

data class Die(
    val value: Int = Random.nextInt(1, 7),
    val isSelected: Boolean = false
)

data class PlayerState(
    val dice: List<Die> = List(5) { Die() },
    val currentScore: Int = 0,
    val totalScore: Int = 0,
    val rollCount: Int = 0
)

data class GameState(
    val humanPlayer: PlayerState = PlayerState(),
    val computerPlayer: PlayerState = PlayerState(),
    val targetScore: Int = 101,
    val gameEnded: Boolean = false,
    val winner: String = "",
    val isTieBreaker: Boolean = false,
    val attemptCount: Int = 0
)