package com.example.wakeupshakeup.model

data class AlarmInfoItem(
    val id: Int,
    val setTime: String, // Time set by the user
    val totalShakeCount: Int,
    val streakCount: Int
)
