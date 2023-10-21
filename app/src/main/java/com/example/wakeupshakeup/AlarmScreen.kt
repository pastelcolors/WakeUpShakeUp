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
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.wakeupshakeup.ShowTimePicker
import com.example.wakeupshakeup.AlarmHelper
import com.example.wakeupshakeup.R
import com.example.wakeupshakeup.ui.Poppins


@Composable
fun Greeting(name: String) {
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
fun WakeUpCard() {
    val time = remember { mutableStateOf("8:45 AM") }
    var isEditing by remember { mutableStateOf(false) }
    val alarmHelper = AlarmHelper(LocalContext.current)

    CardSection(
        title = "Daily wake up time",
        actionText = "Edit",
        actionOnClick = {
            isEditing = !isEditing
        }
    ) {
        if (isEditing) {
            // Display the time picker when in edit mode
            val (selectedHour, selectedMinute) = ShowTimePicker(time)
            alarmHelper.scheduleShakeService(selectedHour, selectedMinute)
            isEditing = false // Exit edit mode automatically

        } else {
            // Display the selected time when not in edit mode
            Text(
                text = time.value,
                fontFamily = Poppins,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun StreakReportCard(streakCount: Int) {
    CardSection(title = "Current streak") {
        Text(
            text = "You have been on time for $streakCount days in a row!",
            fontFamily = Poppins,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
    }
}

@Composable
fun WeeklyShakeCountCard() {
    CardSection(title = "Weekly Shake Count") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "10",
                fontFamily = Poppins,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun RingtoneCard(songTitle: String, songArtist: String) {
    CardSection(title = "Ringtone for the day") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // Adjust the height as needed
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.i_gotta_feeling), // Replace with your image resource
                contentDescription = null, // Provide a content description
                contentScale = ContentScale.Crop, // Adjust the content scale as needed
                modifier = Modifier.width(80.dp) // Adjust the width of the image
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

