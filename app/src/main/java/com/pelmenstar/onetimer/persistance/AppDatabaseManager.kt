package com.pelmenstar.onetimer.persistance

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.pelmenstar.onetimer.OneTimerApplication

private const val DATABASE_NAME = "app"

private val MIGRATION_1_2 = object : Migration(1, 2) {
  override fun migrate(connection: SQLiteConnection) {
    connection.execSQL(
      "CREATE TABLE IF NOT EXISTS `app_settings` " +
        "(`id` INTEGER NOT NULL, `tile_alarm_minutes` INTEGER NOT NULL, PRIMARY KEY(`id`))"
    )
  }
}

internal fun createAppDatabase(context: Context): AppDatabase {
  return Room.databaseBuilder<AppDatabase>(context, DATABASE_NAME)
    .addMigrations(MIGRATION_1_2)
    .build()
}

fun getAppDatabase(context: Context): AppDatabase {
  return (context.applicationContext as OneTimerApplication).database
}
