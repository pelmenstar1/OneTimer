package com.pelmenstar.onetimer.persistance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ActiveAlarmDao {
  @Query("SELECT * FROM active_alarm_info LIMIT 1")
  suspend fun getActiveAlarm(): ActiveAlarmInfo?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setActiveAlarm(value: ActiveAlarmInfo)

  @Query("DELETE FROM active_alarm_info")
  suspend fun clearActiveAlarm(): Int
}
