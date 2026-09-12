package com.pelmenstar.onetimer.activities.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.pelmenstar.onetimer.R
import com.pelmenstar.onetimer.activities.main.screens.home.HomeScreen
import com.pelmenstar.onetimer.activities.main.screens.permission.PermissionScreen
import com.pelmenstar.onetimer.activities.main.screens.settings.SettingsScreen
import com.pelmenstar.onetimer.ui.theme.OneTimerTheme
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

@Serializable
data object SettingsKey : NavKey

@Serializable
data object PermissionKey : NavKey

class MainActivity : ComponentActivity() {
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
            NavDisplay(
              modifier = Modifier.fillMaxSize(),
              backStack = backStack,
              onBack = { backStack.removeLastOrNull() },
              transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
              popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
              predictivePopTransitionSpec = {
                EnterTransition.None togetherWith ExitTransition.None
              },
              entryProvider = entryProvider {
                entry<HomeKey> {
                  HomeScreen(
                    onRequirePermission = { backStack.add(PermissionKey) }
                  )
                }

                entry<SettingsKey> {
                  SettingsScreen()
                }

                entry<PermissionKey> {
                  PermissionScreen(onGranted = { backStack.remove(PermissionKey) })
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
