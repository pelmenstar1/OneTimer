package com.pelmenstar.onetimer.persistance

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [ActiveAlarmInfo::class, AppSettingsEntry::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun activeAlarmDao(): ActiveAlarmDao
  abstract fun appSettingsDao(): AppSettingsDao
}
