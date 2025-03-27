package com.example.dicegame.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppState(
    val humanWins: Int = 0,
    val computerWins: Int = 0
) : Parcelable 