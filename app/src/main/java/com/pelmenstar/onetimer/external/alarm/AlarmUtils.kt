package com.pelmenstar.onetimer.external.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.pelmenstar.onetimer.utils.MS_IN_MINUTE

fun scheduleAlarm(context: Context, futureMinutes: Int): Boolean {
  val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  val durationMs = futureMinutes.toLong() * MS_IN_MINUTE

  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    if (!manager.canScheduleExactAlarms()) {
      return false
    }
  }

  val intent = Intent(context, AlarmBroadcastReceiver::class.java)

  val pendingIntent = PendingIntent.getBroadcast(
    context,
    0,
    intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
  )

  val now = SystemClock.elapsedRealtime()
  val triggerAt = now + durationMs

  manager.setExact(
    AlarmManager.ELAPSED_REALTIME_WAKEUP,
    triggerAt,
    pendingIntent
  )

  return true
}
