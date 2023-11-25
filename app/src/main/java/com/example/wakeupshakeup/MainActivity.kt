package com.example.wakeupshakeup

import Greeting
import ResetButton
import RingtoneCard
import StreakReportCard
import WakeUpCard
import TotalShakeCountCard
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wakeupshakeup.services.ShakeService
import com.example.wakeupshakeup.ui.theme.WakeUpShakeUpTheme
import com.example.wakeupshakeup.AlarmHelper
import com.example.wakeupshakeup.database.DatabaseHandler
import com.example.wakeupshakeup.viewmodel.AlarmViewModel

class MainActivity : ComponentActivity() {
    private val alarmViewModel: AlarmViewModel by viewModels()

    private fun updateTotalShakeCountForTesting(newCount: Int) {
        val sql = "UPDATE ${DatabaseHandler.TABLE_ALARM_INFO} SET ${DatabaseHandler.COLUMN_TOTAL_SHAKE_COUNT} = $newCount WHERE ${DatabaseHandler.COLUMN_ID} = 1"
        alarmViewModel.alarmInfoDatabase.executeRawSql(sql)
        // After updating the database, refresh the LiveData in the ViewModel
        alarmViewModel.loadTotalShakeCount()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            WakeUpShakeUpTheme {
                AlarmScreen(alarmViewModel)
            }
        }

        // updateTotalShakeCountForTesting(42) // Set totalShakeCount to 42 for testing
        alarmViewModel.bindToShakeService()
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmViewModel.unbindFromShakeService()
    }
}

@Composable
fun AlarmScreen(alarmViewModel: AlarmViewModel) {
    val songTitle by alarmViewModel.songTitle.observeAsState("I Gotta Feeling")
    val songArtist by alarmViewModel.songArtist.observeAsState("Black Eyed Peas")
    val totalShakeCount by alarmViewModel.totalShakeCount.observeAsState(0)
    val streakCount by alarmViewModel.streakCount.observeAsState(0)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(R.color.slate_900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Greeting()
                Spacer(modifier = Modifier.weight(1f))
                ResetButton(alarmViewModel)
            }
            Spacer(modifier = Modifier.height(24.dp))
            WakeUpCard(alarmViewModel)
            Spacer(modifier = Modifier.height(24.dp))
            StreakReportCard(streakCount)
            Spacer(modifier = Modifier.height(24.dp))
            TotalShakeCountCard(totalShakeCount)
            Spacer(modifier = Modifier.height(24.dp))
            RingtoneCard(songTitle, songArtist)
            Spacer(modifier = Modifier.height(24.dp))
            ResetButton(alarmViewModel)
        }
    }
}
