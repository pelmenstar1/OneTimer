package com.pelmenstar.onetimer.flow

import android.content.Context
import android.util.Log
import com.pelmenstar.onetimer.external.alarm.cancelScheduledAlarm
import com.pelmenstar.onetimer.external.alarm.scheduleAlarm
import com.pelmenstar.onetimer.external.tile.requestAlarmTileUpdate
import com.pelmenstar.onetimer.persistance.ActiveAlarmDao
import com.pelmenstar.onetimer.persistance.ActiveAlarmInfo
import com.pelmenstar.onetimer.persistance.getAppDatabase
import kotlinx.coroutines.flow.Flow

object AlarmFlow {
  private const val TAG = "AlarmFlow"

  private fun dao(context: Context): ActiveAlarmDao {
    return getAppDatabase(context).activeAlarmDao()
  }

  suspend fun getActive(context: Context): ActiveAlarmInfo? {
    return dao(context).getActiveAlarm()
  }

  /**
   * Returns a flow that emits the current alarm and then each time the alarm is changed,
   * no matter who changed it.
   */
  fun getActiveFlow(context: Context): Flow<ActiveAlarmInfo?> {
    return dao(context).getActiveAlarmFlow()
  }

  suspend fun hasActiveAlarm(context: Context): Boolean {
    return dao(context).activeAlarmCount() > 0
  }

  suspend fun schedule(context: Context, futureMinutes: Int): ActiveAlarmInfo? {
    val scheduledInfo = scheduleAlarm(context, futureMinutes) ?: return null

    val alarmDao = dao(context)

    try {
      val activeInfo = ActiveAlarmInfo(
        ActiveAlarmInfo.DEFAULT_ID, scheduledInfo.triggerAtWallTime
      )

      alarmDao.setActiveAlarm(activeInfo)
      requestAlarmTileUpdate(context)

      return activeInfo
    } catch (e: Exception) {
      try {
        clear(context)
      } catch (_: Exception) {
        // We did our best to return to valid state
      }

      throw e
    }
  }

  private suspend fun clearDatabase(context: Context) {
    val db = getAppDatabase(context)
    val rowsDeleted = db.activeAlarmDao().clearActiveAlarm()
    if (rowsDeleted <= 0) {
      Log.w(TAG, "No active alarm was found")
    }

    requestAlarmTileUpdate(context)
  }

  suspend fun clear(context: Context) {
    cancelScheduledAlarm(context)
    clearDatabase(context)
  }

  suspend fun onAlarmReceived(context: Context) {
    clearDatabase(context)
  }
}
