package com.pelmenstar.onetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.pelmenstar.onetimer.screens.home.HomeScreen
import com.pelmenstar.onetimer.ui.theme.OneTimerTheme

data object Home

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      OneTimerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            val backStack = remember { mutableStateListOf<Any>(Home) }

            NavDisplay(
              modifier = Modifier.fillMaxSize(),
              backStack = backStack,
              onBack = { backStack.removeLastOrNull() },
              entryProvider = { route ->
                when (route) {
                  is Home -> NavEntry(route) {
                    HomeScreen()
                  }

                  else -> NavEntry(key = "unknown") { Text(text = "Unknown route") }
                }
              }
            )
          }
        }
      }
    }
  }
}
