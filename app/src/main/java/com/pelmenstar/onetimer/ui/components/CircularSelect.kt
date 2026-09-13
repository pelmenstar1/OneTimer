package com.pelmenstar.onetimer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import com.pelmenstar.onetimer.utils.inverseLerp
import com.pelmenstar.onetimer.utils.normalizeValueBetween
import kotlin.math.PI
import kotlin.math.atan2

@Stable
fun getValueFromPointerPosition(position: Offset, size: Float): Float {
  val r = size * 0.5f

  val alpha = atan2(position.y - r, position.x - r)
  val cycle = PI.toFloat() * 2f
  val sweepAngle = PI.toFloat() * 1.5f - alpha

  return 1 - ((sweepAngle % cycle) / cycle)
}

@Composable
fun CircularSelect(
  modifier: Modifier,
  value: () -> Float,
  minValue: Float = 0.0f,
  minVisibleValue: Float = minValue,
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
        .pointerInput(Unit) {
          awaitEachGesture {
            val diameter = this.size.width.toFloat()

            // The pointer is sampled far more often than the value, aligned to the step,
            // actually changes, so the unchanged ones are not reported to save a recomposition.
            var lastValue = Float.NaN

            fun processChange(change: PointerInputChange) {
              val value = getValueFromPointerPosition(change.position, diameter)
              val denormalizedValue =
                inverseLerp(value, minValue, minVisibleValue, maxValue, step)

              if (denormalizedValue != lastValue) {
                lastValue = denormalizedValue

                onValueChange(denormalizedValue)
              }

              change.consume()
            }

            // Only the pointer that has started the gesture is tracked: otherwise the pointers
            // of a multitouch fight over the value, each one undoing the change of another.
            val down = awaitFirstDown(requireUnconsumed = false)
            processChange(down)

            drag(down.id, ::processChange)
          }
        }) {

      val normalizedValue =
        normalizeValueBetween(value(), minVisibleValue, maxValue)
      val trackWidthPx = trackWidth.toPx()

      this.drawArc(
        color = trackColor,
        size = this.size,
        startAngle = -90.0f,
        sweepAngle = 360.0f,
        useCenter = false,
        style = Stroke(width = trackWidthPx)
      )
      this.drawArc(
        brush = progressBrush,
        size = this.size,
        startAngle = -90.0f,
        sweepAngle = normalizedValue * 360.0f,
        useCenter = false,
        style = Stroke(width = trackWidthPx, cap = StrokeCap.Round)
      )
    }

    content()
  }
}
