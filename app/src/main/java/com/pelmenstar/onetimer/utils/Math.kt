package com.pelmenstar.onetimer.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.util.lerp
import kotlin.math.round

@Stable
fun normalizeValueBetween(value: Float, min: Float, max: Float): Float {
  return (Math.clamp(value, min, max) - min) / (max - min)
}

@Stable
fun alignMultipleTo(value: Float, step: Float): Float {
  if (step.isNaN()) {
    return value
  }

  val count = round(value / step)

  return step * count
}

@Stable
fun inverseLerp(
  fraction: Float,
  min: Float,
  minVisibleValue: Float,
  max: Float,
  step: Float
): Float {
  val result = lerp(minVisibleValue, max, fraction)

  return Math.clamp(alignMultipleTo(result, step), min, max)
}
