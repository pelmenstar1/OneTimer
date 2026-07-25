package com.pelmenstar.onetimer.persistance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pelmenstar.onetimer.ui.components.MIN_MINUTES

@Entity(tableName = "app_settings")
data class AppSettingsEntry(
  @PrimaryKey val key: String,
  @ColumnInfo(name = "value") val value: String,
) {
  companion object {
    const val KEY_TILE_ALARM_MINUTES = "tile_alarm_minutes"
    const val KEY_LAST_SELECTED_MINUTES = "last_selected_minutes"

    const val DEFAULT_TILE_ALARM_MINUTES = 60
    const val DEFAULT_LAST_SELECTED_MINUTES = MIN_MINUTES
  }
}
