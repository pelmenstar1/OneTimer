package com.pelmenstar.onetimer.persistance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_alarm_info")
data class ActiveAlarmInfo(
  @PrimaryKey val id: Int,
  @ColumnInfo(name = "target_time") val targetTime: Long,
) {
  companion object {
    const val DEFAULT_ID = 0
  }
}
