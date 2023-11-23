package com.example.wakeupshakeup.model

import com.example.wakeupshakeup.R

data class Song(
    val title: String,
    val artist: String,
    val resource: Int
)

val alarmSounds = arrayOf(
    Song("Rather Be", "Clean Bandit", R.raw.ratherbe), // Sunday
    Song("Ready For It", "Taylor Swift", R.raw.readyforit), // Monday
    Song("Shake It Off", "Taylor Swift", R.raw.shakeitoff), // Tuesday
    Song("Turn Up", "Chris Brown", R.raw.turnup), // Wednesday
    Song("Viva La Vida", "Coldplay", R.raw.vivalavida), // Thursday
    Song("I Gotta Feeling", "Black Eyed Peas", R.raw.igottafeeling), // Friday
    Song("We Found Love", "Rihanna", R.raw.wefoundlove) // Saturday
)