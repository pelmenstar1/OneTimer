package com.pelmenstar.onetimer.persistance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
  @PrimaryKey val id: Int,
  /** Amount of minutes the quick tile schedules an alarm for. */
  @ColumnInfo(name = "tile_alarm_minutes") val tileAlarmMinutes: Int,
) {
  companion object {
    const val DEFAULT_ID = 0
    const val DEFAULT_TILE_ALARM_MINUTES = 60

    /** The settings a user, that has never changed anything, has. */
    val DEFAULT = AppSettings(DEFAULT_ID, DEFAULT_TILE_ALARM_MINUTES)
  }
}
