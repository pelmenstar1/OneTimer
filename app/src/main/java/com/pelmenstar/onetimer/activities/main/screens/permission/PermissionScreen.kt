package com.pelmenstar.onetimer.activities.main.screens.permission

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@RequiresApi(31)
fun PermissionScreen(
  onLaunchSettings: () -> Unit = {}
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(text = "The app needs alarm clock permission")

    Button(
      modifier = Modifier.padding(top = 4.dp),
      onClick = {
        onLaunchSettings()
      }
    ) {
      Text(text = "Go to the settings")
    }
  }
}
