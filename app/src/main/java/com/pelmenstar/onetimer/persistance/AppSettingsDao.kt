package com.pelmenstar.onetimer.persistance

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
  @Query("SELECT * FROM app_settings")
  suspend fun getEntries(): Array<AppSettingsEntry>

  @Query("SELECT * FROM app_settings")
  fun entriesFlow(): Flow<Array<AppSettingsEntry>>

  @Query("SELECT * FROM app_settings WHERE `key`=:key")
  suspend fun getEntry(key: String): AppSettingsEntry

  // Upsert, because an entry does not exist until the setting is changed for the first time.
  @Upsert
  suspend fun setEntry(entry: AppSettingsEntry)
}
