package com.pelmenstar.onetimer.flow

import android.content.Context
import com.pelmenstar.onetimer.persistance.AppSettings
import com.pelmenstar.onetimer.persistance.getAppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SettingsFlow {
  suspend fun get(context: Context): AppSettings {
    // The row does not exist until the settings are changed for the first time.
    return getAppDatabase(context).appSettingsDao().getSettings()
      ?: AppSettings.DEFAULT
  }

  fun getFlow(context: Context): Flow<AppSettings> {
    return getAppDatabase(context).appSettingsDao().getSettingsFlow()
      .map { it ?: AppSettings.DEFAULT }
  }

  suspend fun getTileAlarmMinutes(context: Context): Int {
    return get(context).tileAlarmMinutes
  }

  suspend fun setTileAlarmMinutes(context: Context, minutes: Int) {
    val settings = get(context)

    getAppDatabase(context).appSettingsDao().setSettings(
      settings.copy(id = AppSettings.DEFAULT_ID, tileAlarmMinutes = minutes)
    )
  }
}
