package com.pelmenstar.onetimer.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.pelmenstar.onetimer.utils.formatTime

const val MAX_HOURS = 4

const val MIN_MINUTES = 1

const val TOTAL_MINUTES = (MAX_HOURS * 60).toFloat()

/**
 * A circular selector of an amount of minutes, that shows the selected amount in its center.
 *
 * [CircularSelect] keeps the aspect ratio of 1, so a [modifier] limiting the width is enough.
 */
@Composable
fun MinutesSelect(
  modifier: Modifier,
  minutes: Int,
  onMinutesChange: (minutes: Int) -> Unit
) {
  CircularSelect(
    modifier = modifier,
    value = minutes.toFloat(),
    minValue = MIN_MINUTES.toFloat(),
    maxValue = TOTAL_MINUTES,
    step = 1f,
    trackWidth = 25.dp,
    progressBrush = Brush.sweepGradient(
      listOf(Color.Magenta, Color.Red, Color.Magenta)
    ),
    onValueChange = { value -> onMinutesChange(value.toInt()) },
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
}
