import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.wakeupshakeup.AlarmHelper
import com.example.wakeupshakeup.R
import com.example.wakeupshakeup.database.AlarmInfoDatabase
import com.example.wakeupshakeup.ui.Poppins
import com.example.wakeupshakeup.viewmodel.AlarmViewModel

@Composable
fun Greeting() {
    Text(
        text = "Rise and shine!",
        fontFamily = Poppins,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp),
        color = Color.White
    )
}

@Composable
fun WakeUpCard(alarmViewModel: AlarmViewModel) {
    val time by alarmViewModel.setTime.observeAsState("8:45 AM")
    var isEditing by remember { mutableStateOf(false) }
    val alarmHelper = AlarmHelper(LocalContext.current)
    val context = LocalContext.current

    CardSection(
        title = "Daily wake up time",
        icon = R.drawable.sun,
        actionText = "Edit",
        actionOnClick = {
            isEditing = true // Enter edit mode
        }
    ) {
        if (isEditing) {
            // Display the selected time when in edit mode
            Text(
                text = time,
                fontFamily = Poppins,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        } else {
            // Display the selected time when not in edit mode
            Text(
                text = time,
                fontFamily = Poppins,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }

    // Use a side effect to show the time picker when isEditing becomes true
    LaunchedEffect(isEditing) {
        if (isEditing) {
            ShowTimePicker(context, time) { selectedHour, selectedMinute ->
                // Log the selected time for debugging
                Log.d("MainActivity", "Selected time: $selectedHour:$selectedMinute")
                val formattedTime = formatTime(selectedHour, selectedMinute)
                alarmViewModel.modifySetTime(formattedTime)
                alarmHelper.scheduleShakeService(selectedHour, selectedMinute)
                isEditing = false
            }
        }
    }
}

fun ShowTimePicker(
    context: Context,
    time: String,
    onTimeSelected: (Int, Int) -> Unit
) {
    val (prevHour, prevMinute) = parseTime(time)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minuteOfHour ->
            onTimeSelected(hourOfDay, minuteOfHour)
        }, prevHour, prevMinute, false
    )
    timePickerDialog.show()
}


fun parseTime(timeString: String): Pair<Int, Int> {
    val timeParts = timeString.split(" ", ":", limit = 3)
    if (timeParts.size < 3) return Pair(0, 0)

    val hour = timeParts[0].toIntOrNull() ?: 0
    val minute = timeParts[1].toIntOrNull() ?: 0
    val period = timeParts[2]

    val formattedHour = when (period) {
        "PM" -> if (hour < 12) hour + 12 else hour
        "AM" -> if (hour == 12) 0 else hour
        else -> hour
    }

    return Pair(formattedHour, minute)
}

fun formatTime(hourOfDay: Int, minute: Int): String {
    val formattedHour: Int
    val period: String

    when {
        hourOfDay >= 12 -> {
            formattedHour = if (hourOfDay > 12) hourOfDay - 12 else hourOfDay
            period = "PM"
        }
        hourOfDay == 0 -> {
            formattedHour = 12
            period = "AM"
        }
        else -> {
            formattedHour = hourOfDay
            period = "AM"
        }
    }

    return "$formattedHour:${String.format("%02d", minute)} $period"
}

@Composable
fun StreakReportCard(streakCount: Int) {
    CardSection(title = " Current streak", icon = R.drawable.streak) {
        Text(
            text = "You have been on time for $streakCount days in a row!",
            fontFamily = Poppins,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
    }
}

@Composable
fun TotalShakeCountCard(totalShakeCount: Int) {
    CardSection(title = "Total Shake Count", icon = R.drawable.shake) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$totalShakeCount",
                fontFamily = Poppins,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun RingtoneCard(songTitle: String, songArtist: String) {
    CardSection(title = " Ringtone for the day", icon = R.drawable.music) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.sound),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp, 80.dp) // Adjust the width of the image
            )
            Spacer(modifier = Modifier.width(16.dp)) // Add spacing between the image and text
            Column {
                Text(
                    text = songTitle,
                    fontFamily = Poppins,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
                Text(
                    text = songArtist,
                    fontFamily = Poppins,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White)
            }
        }
    }
}

@Composable
fun CardSection(
    title: String,
    actionText: String? = null,
    icon: Int,
    actionOnClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(bottom = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.slate_800),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null, // Provide a content description
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = title,
                        fontFamily = Poppins,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White)
                    Spacer(Modifier.weight(1f))
                    actionText?.let {
                        TextButton(
                            onClick = { actionOnClick?.invoke() },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color.Transparent),
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                        ) {
                            Text(
                                fontFamily = Poppins,
                                modifier = Modifier.drawBehind {
                                    val strokeWidthPx = 1.dp.toPx()
                                    val verticalOffset = size.height - 2.sp.toPx()
                                    drawLine(
                                        color = Color.White,
                                        strokeWidth = strokeWidthPx,
                                        start = Offset(0f, verticalOffset),
                                        end = Offset(size.width, verticalOffset)
                                    )
                                },
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                content()
            }
        }
    }
}

@Composable
fun ResetButton(alarmViewModel: AlarmViewModel) {
    TextButton(
        onClick = {
            alarmViewModel.resetShakeAndStreakCountsToDefaults()
        },
        modifier = Modifier.padding(12.dp)
    ) {
        Text(
            text = "Reset to Defaults",
            color = Color.White,
        )
    }
}
