package com.pelmenstar.onetimer.persistance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveAlarmDao {
  @Query("SELECT * FROM active_alarm_info LIMIT 1")
  suspend fun getActiveAlarm(): ActiveAlarmInfo?

  @Query("SELECT * FROM active_alarm_info LIMIT 1")
  fun getActiveAlarmFlow(): Flow<ActiveAlarmInfo?>

  @Query("SELECT COUNT(*) FROM active_alarm_info")
  suspend fun activeAlarmCount(): Int

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun setActiveAlarm(value: ActiveAlarmInfo)

  @Query("DELETE FROM active_alarm_info")
  suspend fun clearActiveAlarm(): Int
}
