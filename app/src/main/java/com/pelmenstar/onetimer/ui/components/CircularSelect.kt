package com.pelmenstar.onetimer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.lerp
import com.pelmenstar.onetimer.utils.normalizeValueBetween
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.math.withSign

@Stable
fun alignMultipleTo(value: Float, step: Float): Float {
  if (step.isNaN()) {
    return value
  }

  val count = round(value / step)

  return step * count
}

@Stable
fun inverseLerp(fraction: Float, min: Float, max: Float, step: Float): Float {
  val result = lerp(min, max, fraction)

  return alignMultipleTo(result, step)
}

@Stable
fun getValueFromPointerPosition(position: Offset, size: Float): Float {
  val r = size * 0.5f
  val x1 = position.x - r
  val y1 = position.y - r

  if (x1 == 0f) {
    return 0f
  }

  val r2 = r * r
  val x12 = x1 * x1
  val x2 = (r2 * x12) / (x12 + y1 * y1)
  val y2 = r2 - x2

  val tx = sqrt(x2).withSign(x1)
  val ty = sqrt(y2).withSign(y1)

  val alpha = atan2(ty, tx)
  val cycle = PI.toFloat() * 2f
  val sweepAngle = PI.toFloat() * 1.5f - alpha

  return 1 - ((sweepAngle % cycle) / cycle)
}

@Composable
fun CircularSelect(
  modifier: Modifier,
  value: Float,
  minValue: Float = 0.0f,
  maxValue: Float = 1.0f,
  step: Float = Float.NaN,
  trackColor: Color = Color.Gray,
  trackWidth: Dp = Dp(20.0f),
  progressBrush: Brush,
  onValueChange: (value: Float) -> Unit = {},
  content: @Composable (BoxScope.() -> Unit) = {}
) {
  Box(
    modifier = modifier.aspectRatio(1.0f),
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(null) {
          awaitPointerEventScope {
            val diameter = this.size.width.toFloat()

            while (true) {
              val event = awaitPointerEvent()

              for (change in event.changes) {
                val value =
                  getValueFromPointerPosition(change.position, diameter)
                val denormalizedValue =
                  inverseLerp(value, minValue, maxValue, step)

                onValueChange(denormalizedValue)
                change.consume()
              }
            }
          }
        }) {
      val normalizedValue = normalizeValueBetween(value, minValue, maxValue)
      val boxSize = Size(this.size.width, this.size.height)

      this.drawArc(
        color = trackColor,
        size = boxSize,
        startAngle = -90.0f,
        sweepAngle = 360.0f,
        useCenter = false,
        style = Stroke(width = trackWidth.toPx())
      )
      this.drawArc(
        brush = progressBrush,
        size = boxSize,
        startAngle = -90.0f,
        sweepAngle = normalizedValue * 360.0f,
        useCenter = false,
        style = Stroke(width = trackWidth.toPx(), cap = StrokeCap.Round)
      )
    }

    content()
  }
}
