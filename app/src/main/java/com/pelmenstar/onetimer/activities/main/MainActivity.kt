package com.pelmenstar.onetimer.activities.main

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.pelmenstar.onetimer.activities.main.screens.home.HomeScreen
import com.pelmenstar.onetimer.activities.main.screens.permission.PermissionScreen
import com.pelmenstar.onetimer.external.alarm.SettingsScheduleExactAlarmContract
import com.pelmenstar.onetimer.ui.theme.OneTimerTheme
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

@Serializable
data object PermissionKey : NavKey

class MainActivity : ComponentActivity() {
  val settingsAlarmLauncher =
    registerForActivityResult(SettingsScheduleExactAlarmContract()) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val a = ContextCompat.checkSelfPermission(
      this,
      "android.permission.USE_FULL_SCREEN_INTENT"
    )
    println("PERMISSION: $a")

    enableEdgeToEdge()
    setContent {
      OneTimerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            val backStack = rememberNavBackStack(HomeKey)

            if (Build.VERSION.SDK_INT >= 31) {
              DisposableEffect(key1 = null) {
                val broadcastReceiver = object : BroadcastReceiver() {
                  override fun onReceive(context: Context?, intent: Intent?) {
                    backStack.remove(PermissionKey)
                  }
                }

                ContextCompat.registerReceiver(
                  this@MainActivity,
                  broadcastReceiver,
                  IntentFilter(
                    AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
                  ),
                  ContextCompat.RECEIVER_NOT_EXPORTED
                )

                onDispose {
                  this@MainActivity.unregisterReceiver(broadcastReceiver)
                }
              }
            }

            NavDisplay(
              modifier = Modifier.fillMaxSize(),
              backStack = backStack,
              onBack = { backStack.removeLastOrNull() },
              entryProvider = entryProvider {
                entry<HomeKey> {
                  HomeScreen(onRequirePermission = { backStack.add(PermissionKey) })
                }

                if (Build.VERSION.SDK_INT >= 31) {
                  entry<PermissionKey> {
                    PermissionScreen(onLaunchSettings = {
                      settingsAlarmLauncher.launch(Unit)
                    })
                  }
                }
              }
            )
          }
        }
      }
    }
  }
}
