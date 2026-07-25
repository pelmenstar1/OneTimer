package com.pelmenstar.onetimer.flow

import android.content.Context
import android.util.Log
import com.pelmenstar.onetimer.external.alarm.cancelScheduledAlarm
import com.pelmenstar.onetimer.external.alarm.scheduleAlarm
import com.pelmenstar.onetimer.persistance.ActiveAlarmInfo
import com.pelmenstar.onetimer.persistance.getAppDatabase

object AlarmFlow {
  private const val TAG = "AlarmFlow"

  suspend fun getActive(context: Context): ActiveAlarmInfo? {
    return getAppDatabase(context).activeAlarmDao().getActiveAlarm()
  }

  suspend fun schedule(context: Context, futureMinutes: Int): ActiveAlarmInfo? {
    val scheduledInfo = scheduleAlarm(context, futureMinutes)

    if (scheduledInfo != null) {
      val alarmDao = getAppDatabase(context).activeAlarmDao()

      try {
        val activeInfo = ActiveAlarmInfo(
          ActiveAlarmInfo.DEFAULT_ID, scheduledInfo.triggerAt
        )

        alarmDao.setActiveAlarm(activeInfo)

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

    return null
  }

  suspend fun clear(context: Context) {
    cancelScheduledAlarm(context)
    clearDatabase(context)
  }

  private suspend fun clearDatabase(context: Context) {
    val db = getAppDatabase(context)
    val rowsDeleted = db.activeAlarmDao().clearActiveAlarm()
    if (rowsDeleted <= 0) {
      Log.w(TAG, "No active alarm was found")
    }
  }

  suspend fun onAlarmReceived(context: Context) {
    clearDatabase(context)
  }
}
