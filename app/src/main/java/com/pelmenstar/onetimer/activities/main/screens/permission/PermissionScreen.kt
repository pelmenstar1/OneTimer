package com.pelmenstar.onetimer.activities.main.screens.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.pelmenstar.onetimer.external.alarm.SettingsFullScreenIntentContract
import com.pelmenstar.onetimer.external.alarm.SettingsScheduleExactAlarmContract
import com.pelmenstar.onetimer.external.alarm.canPostNotifications
import com.pelmenstar.onetimer.external.alarm.canScheduleExactAlarms
import com.pelmenstar.onetimer.external.alarm.canUseFullScreenIntent

private enum class MissingPermission {
  Notifications,
  FullScreenIntent,
  ExactAlarm
}

@Composable
fun PermissionScreen(onGranted: () -> Unit = {}) {
  val context = LocalContext.current
  var missing by remember { mutableStateOf<MissingPermission?>(null) }

  val refresh = {
    val next = firstMissingPermission(context)
    missing = next

    if (next == null) {
      onGranted()
    }
  }

  LifecycleResumeEffect(Unit) {
    refresh()
    onPauseOrDispose { }
  }

  // The exact alarm permission can additionally be revoked by the system while the
  // screen is in the foreground, which no resume would catch.
  if (Build.VERSION.SDK_INT >= 31) {
    DisposableEffect(context) {
      val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
          refresh()
        }
      }

      ContextCompat.registerReceiver(
        context,
        receiver,
        IntentFilter(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED),
        ContextCompat.RECEIVER_NOT_EXPORTED
      )

      onDispose {
        context.unregisterReceiver(receiver)
      }
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    when (missing) {
      MissingPermission.Notifications -> NotificationsRequest(onRequested = refresh)
      MissingPermission.FullScreenIntent -> FullScreenIntentRequest()
      MissingPermission.ExactAlarm -> ExactAlarmRequest()
      null -> Unit
    }
  }
}

@SuppressLint("InlinedApi")
@Composable
private fun NotificationsRequest(onRequested: () -> Unit) {
  val launcher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
      onRequested()
    }

  Request(
    text = "The app needs notification permission to work",
    buttonText = "OK",
    onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
  )
}

@Composable
private fun FullScreenIntentRequest() {
  val launcher =
    rememberLauncherForActivityResult(SettingsFullScreenIntentContract()) { }

  Request(
    text = "The app needs the full-screen notification permission to show the alarm on its own",
    buttonText = "Go to the settings",
    onClick = { launcher.launch(Unit) }
  )
}

@Composable
private fun ExactAlarmRequest() {
  val launcher =
    rememberLauncherForActivityResult(SettingsScheduleExactAlarmContract()) { }

  Request(
    text = "The app needs alarm clock permission",
    buttonText = "Go to the settings",
    onClick = { launcher.launch(Unit) }
  )
}

@Composable
private fun Request(
  text: String,
  buttonText: String,
  onClick: () -> Unit
) {
  Text(text = text, textAlign = TextAlign.Center)

  Button(
    modifier = Modifier.padding(top = 6.dp),
    onClick = onClick
  ) {
    Text(text = buttonText)
  }
}

private fun firstMissingPermission(context: Context): MissingPermission? {
  return when {
    !canPostNotifications(context) -> MissingPermission.Notifications
    !canUseFullScreenIntent(context) -> MissingPermission.FullScreenIntent
    !canScheduleExactAlarms(context) -> MissingPermission.ExactAlarm
    else -> null
  }
}
