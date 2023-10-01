package com.example.wakeupshakeup.services;

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.util.Calendar
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.wakeupshakeup.R
import kotlin.math.sqrt
import kotlin.random.Random

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

class ShakeService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private var shakeCount = 0
    private val requiredShakes = 10
    private val NOTIFICATION_ID = 1 // Define NOTIFICATION_ID here

    // Define the LiveData for the current song title
    val currentSongTitle = MutableLiveData<String>()
    val currentSongArtist = MutableLiveData<String>()

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensor?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }

        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val todaySound = alarmSounds[dayOfWeek - 1] // Subtract 1 because Calendar.SUNDAY is 1
        mediaPlayer = MediaPlayer.create(this, todaySound.resource)
        mediaPlayer?.start()

        startForeground(NOTIFICATION_ID, createNotification())
        Log.d("ShakeService", "Service started and moved to foreground")
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
            Log.d("ShakeService", "Acceleration: $acceleration")
            if (acceleration > 30) {
                Log.d("ShakeService", "Shake detected")
                Log.d("ShakeService", "Shake acceleration: $acceleration")
                shakeCount++
                if (shakeCount >= requiredShakes) {
                    mediaPlayer?.stop()
                    // log
                    Log.d("ShakeService", "Alarm stopped")
            
                    shakeCount = 0
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "WAKE_UP_SHAKE_UP_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Wake Up Shake Up Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Wake Up Shake Up"

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, notificationChannelId)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Wake Up Shake Up")
            .setContentText("Service is running...")
        return builder.build()
    }

    companion object {
        fun getStartIntent(context: Context): PendingIntent {
            val intent = Intent(context, ShakeService::class.java)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getService(context, 0, intent, flags)
        }
    }
}
