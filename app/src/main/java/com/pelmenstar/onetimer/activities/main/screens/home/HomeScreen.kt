package com.pelmenstar.onetimer.activities.main.screens.home

import android.os.SystemClock
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.pelmenstar.onetimer.flow.AlarmFlow
import com.pelmenstar.onetimer.persistance.ActiveAlarmInfo
import com.pelmenstar.onetimer.ui.components.CircularSelect
import com.pelmenstar.onetimer.utils.formatTime
import kotlinx.coroutines.launch
import java.util.Date

const val MAX_HOURS = 4

const val TOTAL_MINUTES = (MAX_HOURS * 60).toFloat()

private sealed interface AlarmState {
  // Until we know for sure, the alarm is considered to be set.
  data object Loading : AlarmState
  data object NotSet : AlarmState
  data class Set(val info: ActiveAlarmInfo) : AlarmState
}

private fun ActiveAlarmInfo?.toAlarmState(): AlarmState {
  return if (this == null) AlarmState.NotSet else AlarmState.Set(this)
}

@Composable
fun HomeScreen(
  onRequirePermission: () -> Unit = {}
) {
  var state by remember { mutableStateOf<AlarmState>(AlarmState.Loading) }
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  LaunchedEffect(Unit) {
    state = AlarmFlow.getActive(context).toAlarmState()
  }

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

            state = AlarmState.NotSet
          }
        }
      )

      AlarmState.NotSet -> AlarmSetup(
        onSchedule = { minutes ->
          scope.launch {
            val alarmInfo = AlarmFlow.schedule(context, minutes)

            if (alarmInfo != null) {
              state = alarmInfo.toAlarmState()
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
  onSchedule: (minutes: Int) -> Unit
) {
  var minutes by rememberSaveable { mutableIntStateOf(1) }

  CircularSelect(
    modifier = Modifier
      .fillMaxWidth(0.8f)
      .fillMaxHeight(0.5f),
    value = minutes.toFloat(),
    minValue = 1f,
    maxValue = TOTAL_MINUTES,
    step = 1f,
    trackWidth = 25.dp,
    progressBrush = Brush.sweepGradient(
      listOf(Color.Magenta, Color.Red, Color.Magenta)
    ),
    onValueChange = { value -> minutes = value.toInt() },
  ) {
    Text(
      text = formatTime(minutes),
      style = TextStyle(
        fontWeight = FontWeight(900),
        fontSize = TextUnit(
          40f,
          TextUnitType.Sp
        )
      )
    )
  }

  Button(onClick = { onSchedule(minutes) }) {
    Text(text = "Set an alarm")
  }
}

@Composable
private fun ScheduledAlarm(
  info: ActiveAlarmInfo,
  onCancel: () -> Unit
) {
  val context = LocalContext.current

  // targetTime is based on SystemClock.elapsedRealtime(), convert it to the wall clock.
  val targetText = remember(info) {
    val wallTime =
      System.currentTimeMillis() + (info.targetTime - SystemClock.elapsedRealtime())

    DateFormat.getTimeFormat(context).format(Date(wallTime))
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
