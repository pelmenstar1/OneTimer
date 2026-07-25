package com.pelmenstar.onetimer.external.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.pelmenstar.onetimer.utils.MS_IN_MINUTE

/** [triggerAtWallTime] is a [System.currentTimeMillis] based time. */
data class ScheduledAlarmInfo(val triggerAtWallTime: Long)

private fun getAlarmManager(context: Context): AlarmManager {
  return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

private fun getBroadcastPendingIntent(context: Context): PendingIntent {
  val intent = Intent(context, AlarmBroadcastReceiver::class.java)

  return PendingIntent.getBroadcast(
    context,
    0,
    intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
  )
}

fun scheduleAlarm(context: Context, futureMinutes: Int): ScheduledAlarmInfo? {
  val manager = getAlarmManager(context)
  val durationMs = futureMinutes.toLong() * MS_IN_MINUTE

  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    if (!manager.canScheduleExactAlarms()) {
      return null
    }
  }

  val pendingIntent = getBroadcastPendingIntent(context)

  // The alarm itself is scheduled on the monotonic clock, so that changing
  // the system time does not shift it.
  manager.setExact(
    AlarmManager.ELAPSED_REALTIME_WAKEUP,
    SystemClock.elapsedRealtime() + durationMs,
    pendingIntent
  )

  return ScheduledAlarmInfo(System.currentTimeMillis() + durationMs)
}

fun cancelScheduledAlarm(context: Context) {
  val manager = getAlarmManager(context)
  val pendingIntent = getBroadcastPendingIntent(context)

  manager.cancel(pendingIntent)
}
