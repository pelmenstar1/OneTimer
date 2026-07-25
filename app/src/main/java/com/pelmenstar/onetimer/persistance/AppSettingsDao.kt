package com.pelmenstar.onetimer.persistance

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
  @Query("SELECT * FROM app_settings LIMIT 1")
  suspend fun getSettings(): AppSettings?

  @Query("SELECT * FROM app_settings LIMIT 1")
  fun getSettingsFlow(): Flow<AppSettings?>

  @Upsert
  suspend fun setSettings(value: AppSettings)
}
