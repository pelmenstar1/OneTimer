package com.pelmenstar.onetimer.activities.main.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.pelmenstar.onetimer.external.alarm.hasAlarmPermissions
import com.pelmenstar.onetimer.flow.AlarmFlow
import com.pelmenstar.onetimer.flow.SettingsFlow
import com.pelmenstar.onetimer.persistance.ActiveAlarmInfo
import com.pelmenstar.onetimer.ui.components.MinutesSelect
import com.pelmenstar.onetimer.utils.formatTimeFromWallTime
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private sealed interface AlarmState {
  // Until we know for sure, the alarm is considered to be set.
  data object Loading : AlarmState
  data class NotSet(val lastSelectedMinutes: Int) : AlarmState
  data class Set(val info: ActiveAlarmInfo) : AlarmState
}

@Composable
fun HomeScreen(
  onRequirePermission: () -> Unit = {}
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  // The state is Loading until both the alarm and the settings are known, so that
  // the selector is not shown with a stale amount of minutes.
  val stateFlow = remember(context) {
    combine(
      AlarmFlow.getActiveFlow(context),
      SettingsFlow.getFlow(context)
    ) { info, settings ->
      if (info == null) {
        AlarmState.NotSet(settings.lastSelectedMinutes)
      } else {
        AlarmState.Set(info)
      }
    }
  }
  val state by stateFlow.collectAsState(initial = AlarmState.Loading)

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    when (val currentState = state) {
      AlarmState.Loading -> CircularProgressIndicator()

      is AlarmState.Set -> ScheduledAlarm(
        info = currentState.info,
        onCancel = {
          scope.launch {
            AlarmFlow.clear(context)
          }
        }
      )

      is AlarmState.NotSet -> AlarmSetup(
        initialMinutes = currentState.lastSelectedMinutes,
        onSchedule = { minutes ->
          scope.launch {
            if (hasAlarmPermissions(context) &&
              AlarmFlow.schedule(context, minutes) != null
            ) {
              SettingsFlow.setLastSelectedMinutes(context, minutes)
            } else {
              onRequirePermission()
            }
          }
        }
      )
    }
  }
}

@Composable
private fun AlarmSetup(
  initialMinutes: Int,
  onSchedule: (minutes: Int) -> Unit
) {
  var minutes by rememberSaveable(initialMinutes) {
    mutableIntStateOf(
      initialMinutes
    )
  }

  MinutesSelect(
    modifier = Modifier
      .fillMaxWidth(0.8f)
      .fillMaxHeight(0.5f),
    minutes = { minutes },
    onMinutesChange = { value -> minutes = value }
  )

  Button(
    modifier = Modifier.padding(top = 10.dp),
    onClick = { onSchedule(minutes) }) {
    Text(text = "Set an alarm")
  }
}

@Composable
private fun ScheduledAlarm(
  info: ActiveAlarmInfo,
  onCancel: () -> Unit
) {
  val context = LocalContext.current

  val targetText = remember(info) {
    formatTimeFromWallTime(context, info.targetTime)
  }

  Text(
    text = targetText,
    modifier = Modifier.padding(bottom = 6.dp),
    style = TextStyle(
      fontWeight = FontWeight(900),
      fontSize = TextUnit(
        40f,
        TextUnitType.Sp
      )
    )
  )

  Button(onClick = onCancel) {
    Text(text = "Cancel the alarm")
  }
}
