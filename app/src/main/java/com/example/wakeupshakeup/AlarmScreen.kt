import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun Greeting(name: String) {
    Text(
        text = "Rise and shine",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun WakeUpCard() {
    CardSection(
        title = "Daily wake up time",
        actionText = "Edit",
        actionOnClick = { /* Handle Edit click */ }
    ) {
        // This is the main content for the card
        Text(
            text = "8:45 AM",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StreakReportCard(streakCount: Int) {
    CardSection(title = "Current streak") {
        Text(
            text = "You have been on time for $streakCount days in a row!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DayCircle(day: String, color: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = Color.Black,  // Here, we change the text color to black for better readability
            style = MaterialTheme.typography.labelSmall
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
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RingtoneCard(songTitle: String, songArtist: String) {
    CardSection(title = "Ringtone for the week") {
        Text(text = songTitle, style = MaterialTheme.typography.titleMedium)
        Text(text = songArtist, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun CardSection(
    title: String,
    actionText: String? = null,
    actionOnClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                actionText?.let {
                    TextButton(
                        onClick = { actionOnClick?.invoke() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(it)
                    }
                }
            }

            content()
        }
    }
}