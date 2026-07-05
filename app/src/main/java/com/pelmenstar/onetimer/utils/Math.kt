package com.pelmenstar.onetimer.utils

import androidx.compose.runtime.Stable

@Stable
fun normalizeValueBetween(value: Float, min: Float, max: Float): Float {
  return (Math.clamp(value, min, max) - min) / (max - min)
}
