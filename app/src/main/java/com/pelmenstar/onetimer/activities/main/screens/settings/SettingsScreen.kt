package com.pelmenstar.onetimer.activities.main.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pelmenstar.onetimer.R
import com.pelmenstar.onetimer.flow.SettingsFlow
import com.pelmenstar.onetimer.persistance.AppSettings
import com.pelmenstar.onetimer.ui.components.MinutesSelect
import com.pelmenstar.onetimer.utils.formatTime
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  val settingsFlow = remember(context) { SettingsFlow.getFlow(context) }
  val settings by settingsFlow.collectAsState(initial = null)

  var isSelectingMinutes by remember { mutableStateOf(false) }
  var draftMinutes by remember { mutableIntStateOf(AppSettings.DEFAULT_TILE_ALARM_MINUTES) }

  val currentSettings = settings

  if (currentSettings == null) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      CircularProgressIndicator()
    }

    return
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    SettingItem(
      title = stringResource(R.string.settings_tile_alarm_duration),
      value = formatTime(currentSettings.tileAlarmMinutes),
      onClick = {
        draftMinutes = currentSettings.tileAlarmMinutes
        isSelectingMinutes = true
      }
    )
  }

  if (isSelectingMinutes) {
    MinutesSelectSheet(
      minutes = draftMinutes,
      onMinutesChange = { value -> draftMinutes = value },
      onDismiss = { isSelectingMinutes = false },
      onConfirm = {
        isSelectingMinutes = false

        scope.launch {
          SettingsFlow.setTileAlarmMinutes(context, draftMinutes)
        }
      }
    )
  }
}

@Composable
private fun SettingItem(
  title: String,
  value: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = title, style = MaterialTheme.typography.bodyLarge)

    Text(
      text = value,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.primary
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MinutesSelectSheet(
  minutes: Int,
  onMinutesChange: (minutes: Int) -> Unit,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  // The selector needs a lot of space, so the sheet is never shown partially expanded.
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      MinutesSelect(
        modifier = Modifier.fillMaxWidth(0.8f),
        minutes = minutes,
        onMinutesChange = onMinutesChange
      )

      Button(
        modifier = Modifier.padding(top = 32.dp),
        onClick = onConfirm
      ) {
        Text(text = stringResource(R.string.save))
      }
    }
  }
}
