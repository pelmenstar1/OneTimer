package com.pelmenstar.onetimer.activities.main.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.pelmenstar.onetimer.external.alarm.scheduleAlarm
import com.pelmenstar.onetimer.ui.components.CircularSelect
import com.pelmenstar.onetimer.utils.formatTime

const val MAX_HOURS = 4

const val TOTAL_MINUTES = (MAX_HOURS * 60).toFloat()

@Composable
fun HomeScreen(
  onRequirePermission: () -> Unit = {}
) {
  var minutes by rememberSaveable { mutableIntStateOf(0) }
  val context = LocalContext.current

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    CircularSelect(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .fillMaxHeight(0.5f),
      value = minutes.toFloat(),
      maxValue = TOTAL_MINUTES,
      step = 1.0f,
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

    Button(onClick = {
      val isSuccess = scheduleAlarm(context, minutes)
      if (!isSuccess) {
        onRequirePermission()
      }
    }) {
      Text(text = "Set an alarm")
    }
  }
}
