package com.pelmenstar.onetimer.flow

import android.content.Context
import com.pelmenstar.onetimer.persistance.AppSettings
import com.pelmenstar.onetimer.persistance.AppSettingsDao
import com.pelmenstar.onetimer.persistance.AppSettingsEntry
import com.pelmenstar.onetimer.persistance.getAppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SettingsFlow {
  private fun dao(context: Context): AppSettingsDao {
    return getAppDatabase(context).appSettingsDao()
  }

  private fun findEntryValue(
    entries: Array<AppSettingsEntry>,
    key: String
  ): String? {
    return entries.find { it.key == key }?.value
  }

  private inline fun <T> findEntry(
    entries: Array<AppSettingsEntry>,
    key: String,
    defaultValue: T,
    parse: (value: String) -> T
  ): T {
    val value = findEntryValue(entries, key)
    if (value != null) {
      return parse(value)
    }

    return defaultValue
  }

  private fun parseEntriesToSettings(entries: Array<AppSettingsEntry>): AppSettings {
    val tileAlarmMinutes = findEntry(
      entries,
      AppSettingsEntry.KEY_TILE_ALARM_MINUTES,
      AppSettingsEntry.DEFAULT_TILE_ALARM_MINUTES
    ) { it.toInt() }

    val lastSelectedMinutes = findEntry(
      entries,
      AppSettingsEntry.KEY_LAST_SELECTED_MINUTES,
      AppSettingsEntry.DEFAULT_LAST_SELECTED_MINUTES
    ) { it.toInt() }

    return AppSettings(tileAlarmMinutes, lastSelectedMinutes)
  }

  private suspend fun setEntry(context: Context, key: String, value: String) {
    dao(context).setEntry(AppSettingsEntry(key, value))
  }

  suspend fun get(context: Context): AppSettings {
    val entries = dao(context).getEntries()

    return parseEntriesToSettings(entries)
  }

  fun getFlow(context: Context): Flow<AppSettings> {
    return dao(context)
      .entriesFlow()
      .map { parseEntriesToSettings(it) }
  }

  suspend fun getTileAlarmMinutes(context: Context): Int {
    return get(context).tileAlarmMinutes
  }

  suspend fun setTileAlarmMinutes(context: Context, minutes: Int) {
    setEntry(
      context,
      AppSettingsEntry.KEY_TILE_ALARM_MINUTES,
      minutes.toString()
    )
  }

  suspend fun setLastSelectedMinutes(context: Context, minutes: Int) {
    setEntry(
      context,
      AppSettingsEntry.KEY_LAST_SELECTED_MINUTES,
      minutes.toString()
    )
  }
}
