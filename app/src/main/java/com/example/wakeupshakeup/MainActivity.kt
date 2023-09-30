package com.example.wakeupshakeup

import Greeting
import OnTimeReportCard
import RingtoneCard
import WakeUpCard
import WeeklyShakeCountCard
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wakeupshakeup.ui.theme.WakeUpShakeUpTheme

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
fun AlarmScreen() {
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
            OnTimeReportCard()
            Spacer(modifier = Modifier.height(24.dp))
            WeeklyShakeCountCard()
            Spacer(modifier = Modifier.height(24.dp))
            RingtoneCard()
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