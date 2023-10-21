package com.example.wakeupshakeup

import Greeting
import RingtoneCard
import StreakReportCard
import WakeUpCard
import WeeklyShakeCountCard
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wakeupshakeup.services.ShakeService
import com.example.wakeupshakeup.ui.theme.WakeUpShakeUpTheme
import com.example.wakeupshakeup.AlarmHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WakeUpShakeUpTheme {
                // A surface container using the 'background' color from the theme
                AlarmScreen()
            }
        }
    }
}

@Composable
fun ShowTimePicker(time: MutableState<String>): Pair<Int, Int> {
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    var formattedHour = 0

    val timePickerDialog = TimePickerDialog(
        LocalContext.current, // Use LocalContext.current
        { _, hourOfDay, minute ->
            if (hourOfDay >= 12) {
                if (hourOfDay > 12) {
                    // If it's a PM time after 12:00, subtract 12 hours.
                    formattedHour = hourOfDay - 12
                } else {
                    // If it's exactly 12:00 PM, keep it as is.
                    hourOfDay
                }
                // Set the period (AM/PM) based on the hourOfDay
                val period = "PM"

                // Construct the time string
                time.value = "$formattedHour:${String.format("%02d", minute)} $period"

            } else {
                val period = "AM"
                if (hourOfDay == 0) {
                    formattedHour = 12
                    time.value = "$formattedHour:${String.format("%02d", minute)} $period"
                } else {
                    time.value = "$hourOfDay:${String.format("%02d", minute)} $period"
                }
            }
        }, hour, minute, false
    )
    timePickerDialog.show()

    // Return the selected hour and minute as a pair
    return Pair(hour, minute)
}




@Composable
fun AlarmScreen() {
    val songTitle by ShakeService().currentSongTitle.observeAsState("I Gotta Feeling")
    val songArtist by ShakeService().currentSongArtist.observeAsState("Black Eyed Peas")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Greeting(name = "User")
            Spacer(modifier = Modifier.height(24.dp))
            WakeUpCard()
            Spacer(modifier = Modifier.height(24.dp))
            StreakReportCard(5)
            Spacer(modifier = Modifier.height(24.dp))
            WeeklyShakeCountCard()
            Spacer(modifier = Modifier.height(24.dp))
            RingtoneCard(songTitle, songArtist)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    WakeUpShakeUpTheme {
        AlarmScreen()
    }
}