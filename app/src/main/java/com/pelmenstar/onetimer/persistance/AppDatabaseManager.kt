package com.pelmenstar.onetimer.persistance

import android.content.Context
import androidx.room.Room
import com.pelmenstar.onetimer.OneTimerApplication

private const val DATABASE_NAME = "app"

internal fun createAppDatabase(context: Context): AppDatabase {
  // There are no migrations for now: an incompatible database is simply dropped.
  return Room.databaseBuilder<AppDatabase>(context, DATABASE_NAME)
    .fallbackToDestructiveMigration(dropAllTables = true)
    .build()
}

fun getAppDatabase(context: Context): AppDatabase {
  return (context.applicationContext as OneTimerApplication).database
}
