package com.example.wakeupshakeup.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
  companion object {
      private const val DATABASE_VERSION = 1
      private const val DATABASE_NAME = "AlarmInfoDatabase"
      const val TABLE_ALARM_INFO = "AlarmInfo"
      const val COLUMN_ID = "id"
      const val COLUMN_SET_TIME = "setTime"
      const val COLUMN_TOTAL_SHAKE_COUNT = "totalShakeCount"
      const val COLUMN_STREAK_COUNT = "streakCount"
    }

  override fun onCreate(db: SQLiteDatabase?) {
    val CREATE_ALARM_INFO_TABLE = ("CREATE TABLE " + TABLE_ALARM_INFO + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_SET_TIME + " TEXT,"
            + COLUMN_TOTAL_SHAKE_COUNT + " INTEGER,"
            + COLUMN_STREAK_COUNT + " INTEGER" + ")")
    db?.execSQL(CREATE_ALARM_INFO_TABLE)

    val defaultValues = ContentValues().apply {
        put(COLUMN_ID, 1) // Hardcoded ID
        put(COLUMN_SET_TIME, "08:00 AM") // Default time
        put(COLUMN_TOTAL_SHAKE_COUNT, 0)
        put(COLUMN_STREAK_COUNT, 0)
    }
    db?.insert(TABLE_ALARM_INFO, null, defaultValues)
  }
  
  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
      db!!.execSQL("DROP TABLE IF EXISTS $TABLE_ALARM_INFO")
      onCreate(db)
  }

  // For testing purposes
  fun executeRawSql(sql: String) {
      val db = this.writableDatabase
      db.execSQL(sql)
  }
}