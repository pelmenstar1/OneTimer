package com.pelmenstar.onetimer.external.alarm

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun canPostNotifications(context: Context): Boolean {
  if (Build.VERSION.SDK_INT < 33) {
    return true
  }

  return ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.POST_NOTIFICATIONS
  ) == PackageManager.PERMISSION_GRANTED
}


fun canUseFullScreenIntent(context: Context): Boolean {
  if (Build.VERSION.SDK_INT < 34) {
    return true
  }

  val manager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  return manager.canUseFullScreenIntent()
}

fun canScheduleExactAlarms(context: Context): Boolean {
  if (Build.VERSION.SDK_INT < 31) {
    return true
  }

  return getAlarmManager(context).canScheduleExactAlarms()
}

/** Every permission the alarm needs to be scheduled and to show itself on its own. */
fun hasAlarmPermissions(context: Context): Boolean {
  return canPostNotifications(context) &&
    canUseFullScreenIntent(context) &&
    canScheduleExactAlarms(context)
}
