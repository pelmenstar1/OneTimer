package com.pelmenstar.onetimer.persistance

import android.content.Context
import androidx.room.Room
import com.pelmenstar.onetimer.OneTimerApplication

private const val DATABASE_NAME = "app"

internal fun createAppDatabase(context: Context): AppDatabase {
  return Room.databaseBuilder<AppDatabase>(context, DATABASE_NAME).build()
}

fun getAppDatabase(context: Context): AppDatabase {
  return (context.applicationContext as OneTimerApplication).database
}
