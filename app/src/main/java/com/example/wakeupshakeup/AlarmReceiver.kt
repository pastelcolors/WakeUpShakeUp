package com.example.wakeupshakeup;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.wakeupshakeup.services.ShakeService

class AlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
      val shakeServiceIntent = Intent(context, ShakeService::class.java)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(shakeServiceIntent)
      } else {
          context.startService(shakeServiceIntent)
      }
  }
}
