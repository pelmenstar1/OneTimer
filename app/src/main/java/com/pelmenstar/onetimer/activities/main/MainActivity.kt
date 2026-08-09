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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.pelmenstar.onetimer.R
import com.pelmenstar.onetimer.activities.main.screens.home.HomeScreen
import com.pelmenstar.onetimer.activities.main.screens.permission.PermissionScreen
import com.pelmenstar.onetimer.activities.main.screens.settings.SettingsScreen
import com.pelmenstar.onetimer.external.alarm.SettingsScheduleExactAlarmContract
import com.pelmenstar.onetimer.ui.theme.OneTimerTheme
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

@Serializable
data object PermissionKey : NavKey

@Serializable
data object SettingsKey : NavKey

class MainActivity : ComponentActivity() {
  val settingsAlarmLauncher =
    registerForActivityResult(SettingsScheduleExactAlarmContract()) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      OneTimerTheme {
        val backStack = rememberNavBackStack(HomeKey)

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = {
            MainTopBar(
              isHome = backStack.lastOrNull() == HomeKey,
              onBack = { backStack.removeLastOrNull() },
              onOpenSettings = { backStack.add(SettingsKey) }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
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

                entry<SettingsKey> {
                  SettingsScreen()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
  isHome: Boolean,
  onBack: () -> Unit,
  onOpenSettings: () -> Unit
) {
  TopAppBar(
    title = { Text(text = stringResource(R.string.app_name)) },
    navigationIcon = {
      if (!isHome) {
        IconButton(onClick = onBack) {
          Icon(
            painter = painterResource(R.drawable.baseline_arrow_back_24),
            contentDescription = stringResource(R.string.back_content_description)
          )
        }
      }
    },
    actions = {
      if (isHome) {
        IconButton(onClick = onOpenSettings) {
          Icon(
            painter = painterResource(R.drawable.baseline_settings_24),
            contentDescription = stringResource(R.string.settings_content_description)
          )
        }
      }
    }
  )
}
