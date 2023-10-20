package com.example.wakeupshakeup

import Greeting
import RingtoneCard
import StreakReportCard
import WakeUpCard
import WeeklyShakeCountCard
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wakeupshakeup.services.ShakeService
import com.example.wakeupshakeup.ui.theme.WakeUpShakeUpTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleShakeService() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )

        // Set the alarm to start at 7:30 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 15)
        }

        // Check if the alarm time has already passed for today
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            // If it has, add one day to the calendar to set the alarm for tomorrow
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.d("MainActivity", "Alarm scheduled for: ${calendar.timeInMillis}")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WakeUpShakeUpTheme {
                // A surface container using the 'background' color from the theme
                AlarmScreen()
            }
        }

        val shakeService: ShakeService = ShakeService()

        scheduleShakeService()
    }
}
@Composable
fun ShowTimePicker(context: Context, time: MutableState<String>) {
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    var formattedHour = 0

    val timePickerDialog = TimePickerDialog(
        context,
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
                }
                else {
                    time.value = "$hourOfDay:${String.format("%02d", minute)} $period"
                }
            }

        }, hour, minute, false
    )
    timePickerDialog.show()
}


@Composable
fun AlarmScreen() {
    val songTitle by ShakeService().currentSongTitle.observeAsState("")
    val songArtist by ShakeService().currentSongArtist.observeAsState("")

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