package com.example.wakeupshakeup.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.wakeupshakeup.model.AlarmInfoItem

class AlarmInfoDatabase(context: Context) {
    private var databaseHandler: DatabaseHandler = DatabaseHandler(context)

    fun incrementTotalShakeCount() {
        val db = databaseHandler.writableDatabase
        db.execSQL("UPDATE ${DatabaseHandler.TABLE_ALARM_INFO} SET ${DatabaseHandler.COLUMN_TOTAL_SHAKE_COUNT} = ${DatabaseHandler.COLUMN_TOTAL_SHAKE_COUNT} + 1 WHERE ${DatabaseHandler.COLUMN_ID} = 1")
    }

    fun modifySetTime(newTime: String) {
        val db = databaseHandler.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHandler.COLUMN_SET_TIME, newTime)
        // Use hardcoded ID of 1
        db.update(DatabaseHandler.TABLE_ALARM_INFO, contentValues, "${DatabaseHandler.COLUMN_ID} = ?", arrayOf("1"))
    }

    fun incrementStreakCount() {
        val db = databaseHandler.writableDatabase
        // Use hardcoded ID of 1
        db.execSQL("UPDATE ${DatabaseHandler.TABLE_ALARM_INFO} SET ${DatabaseHandler.COLUMN_STREAK_COUNT} = ${DatabaseHandler.COLUMN_STREAK_COUNT} + 1 WHERE ${DatabaseHandler.COLUMN_ID} = 1")
    }

    fun resetShakeAndStreakCount() {
        val db = databaseHandler.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHandler.COLUMN_TOTAL_SHAKE_COUNT, 0)
        contentValues.put(DatabaseHandler.COLUMN_STREAK_COUNT, 0)
        // Use hardcoded ID of 1
        db.update(DatabaseHandler.TABLE_ALARM_INFO, contentValues, "${DatabaseHandler.COLUMN_ID} = ?", arrayOf("1"))
    }

    fun resetStreakCount() {
        val db = databaseHandler.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHandler.COLUMN_STREAK_COUNT, 0)
        db.update(DatabaseHandler.TABLE_ALARM_INFO, contentValues, "${DatabaseHandler.COLUMN_ID} = ?", arrayOf("1"))
    }

    fun getTotalShakeCount(): Int {
        val db = databaseHandler.readableDatabase
        val cursor = db.rawQuery("SELECT ${DatabaseHandler.COLUMN_TOTAL_SHAKE_COUNT} FROM ${DatabaseHandler.TABLE_ALARM_INFO} WHERE ${DatabaseHandler.COLUMN_ID} = 1", null)
        var count = 0
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(DatabaseHandler.COLUMN_TOTAL_SHAKE_COUNT)
            if (columnIndex >= 0) {
                count = cursor.getInt(columnIndex)
            } else {
                Log.e("DatabaseError", "Column not found: ${DatabaseHandler.COLUMN_TOTAL_SHAKE_COUNT}")
            }
        }
        cursor.close()
        return count
    }

    fun getSetTime(): String {
        val db = databaseHandler.readableDatabase
        val cursor = db.rawQuery("SELECT ${DatabaseHandler.COLUMN_SET_TIME} FROM ${DatabaseHandler.TABLE_ALARM_INFO} WHERE ${DatabaseHandler.COLUMN_ID} = 1", null)
        var setTime = ""
        if (cursor.moveToFirst()) {
            setTime = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_SET_TIME))
        }
        cursor.close()
        return setTime
    }

    fun getStreakCount(): Int {
        val db = databaseHandler.readableDatabase
        val cursor = db.rawQuery("SELECT ${DatabaseHandler.COLUMN_STREAK_COUNT} FROM ${DatabaseHandler.TABLE_ALARM_INFO} WHERE ${DatabaseHandler.COLUMN_ID} = 1", null)
        var count = 0
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(DatabaseHandler.COLUMN_STREAK_COUNT)
            if (columnIndex >= 0) {
                count = cursor.getInt(columnIndex)
            } else {
                Log.e("DatabaseError", "Column not found: ${DatabaseHandler.COLUMN_STREAK_COUNT}")
            }
        }
        cursor.close()
        return count
    }

    // For testing purposes
    fun executeRawSql(sql: String) {
        val db = databaseHandler.writableDatabase
        db.execSQL(sql)
    }

    // fun saveAlarmInfo(alarmInfoItem: AlarmInfoItem) {
    //     val db = databaseHandler.writableDatabase
    //     val values = ContentValues().apply {
    //         put(DatabaseHandler.COLUMN_SET_TIME, alarmInfoItem.setTime)
    //         put(DatabaseHandler.COLUMN_TOTAL_SHAKE_COUNT, alarmInfoItem.totalShakeCount)
    //         put(DatabaseHandler.COLUMN_STREAK_COUNT, alarmInfoItem.streakCount)
    //     }

    //     db.update(DatabaseHandler.TABLE_ALARM_INFO, values, "${DatabaseHandler.COLUMN_ID} = ?", arrayOf("1"))
    // }
}
