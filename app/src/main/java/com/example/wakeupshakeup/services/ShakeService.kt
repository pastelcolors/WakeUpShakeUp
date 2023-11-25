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
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.wakeupshakeup.R
import com.example.wakeupshakeup.database.AlarmInfoDatabase
import com.example.wakeupshakeup.model.Song
import kotlin.math.sqrt
import kotlin.random.Random
import com.example.wakeupshakeup.model.alarmSounds

interface ShakeListener {
    fun onShakeCountChanged(count: Int)
}

class ShakeService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private var shakeCount = 0
    private val requiredShakes = 10
    private val NOTIFICATION_ID = 1
    private var todaySound: Song? = null
    private var lastShakeTimestamp: Long = 0
    var shakeListener: ShakeListener? = null

    val currentSongTitle = MutableLiveData<String>()
    val currentSongArtist = MutableLiveData<String>()

    private var mediaPlayer: MediaPlayer? = null

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): ShakeService = this@ShakeService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensor?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // val dayOfWeek = 6 // For testing purposes, set the day of the week to Saturday
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        todaySound = alarmSounds[dayOfWeek - 1] // Subtract 1 because Calendar.SUNDAY is 1

        // Update the current song information
        currentSongTitle.postValue(todaySound!!.title)
        currentSongArtist.postValue(todaySound!!.artist)

        startForeground(NOTIFICATION_ID, createNotification())
        Log.d("ShakeService", "Service started and moved to foreground")
    }
    
    private fun startAlarm() {
        todaySound?.let { sound ->
            mediaPlayer = MediaPlayer.create(this, sound.resource).apply {
                isLooping = true // Set the MediaPlayer to loop the sound
                start()
            }
        }
    }    

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "com.example.wakeupshakeup.START_ALARM") {
            startAlarm()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val currentTime = System.currentTimeMillis()
                if ((currentTime - lastShakeTimestamp) > 300) { // Debounce check
                    val acceleration = calculateAcceleration(it.values)
                    Log.d("ShakeService", "Acceleration: $acceleration")
                    if (acceleration > 15) {
                        Log.d("ShakeService", "Shake detected")
                        if (mediaPlayer?.isPlaying == true) {
                            incrementShakeCount()
                        }
                        lastShakeTimestamp = currentTime // Update last shake timestamp
                    }
                }
            }
        }
    }
    
    private val alpha = 0.8f
    private var gravity = FloatArray(3)

    private fun calculateAcceleration(values: FloatArray): Float {
        // Apply low-pass filter
        gravity[0] = alpha * gravity[0] + (1 - alpha) * values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * values[2]

        // Remove gravity contribution with the high-pass filter
        val linearAcceleration = FloatArray(3)
        linearAcceleration[0] = values[0] - gravity[0]
        linearAcceleration[1] = values[1] - gravity[1]
        linearAcceleration[2] = values[2] - gravity[2]

        // Calculate the magnitude of the acceleration vector
        return sqrt(
            linearAcceleration[0] * linearAcceleration[0] +
            linearAcceleration[1] * linearAcceleration[1] +
            linearAcceleration[2] * linearAcceleration[2]
        )
    }

    
    private fun incrementShakeCount() {
        shakeCount++
        Log.d("ShakeService", "Shake count: $shakeCount")
        // Get an instance of AlarmInfoDatabase
        val alarmInfoDatabase = AlarmInfoDatabase(this)
        // Increment the total shake count in the database
        alarmInfoDatabase.incrementTotalShakeCount()

        // Get the updated shake count from the database
        val updatedShakeCount = alarmInfoDatabase.getTotalShakeCount()
        // Notify the ViewModel about the updated shake count
        shakeListener?.onShakeCountChanged(updatedShakeCount)

        if (shakeCount >= requiredShakes) {
            stopMediaPlayerAndResetShakeCount()
        }
    }
    
    private fun stopMediaPlayerAndResetShakeCount() {
        mediaPlayer?.apply {
            stop() // Stop the sound
            reset() // Reset the MediaPlayer to its uninitialized state
        }
        // Reset shakeCount for the next alarm
        shakeCount = 0
        Log.d("ShakeService", "Alarm stopped")
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
