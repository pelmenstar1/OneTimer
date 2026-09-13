package com.pelmenstar.onetimer.external.vibrator

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

fun getDefaultVibrator(context: Context): Vibrator {
  if (Build.VERSION.SDK_INT >= 31) {
    val vibratorManager =
      context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

    return vibratorManager.defaultVibrator
  }

  @Suppress("DEPRECATION")
  return context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}

fun Vibrator.vibrateWaveform(timings: LongArray, repeat: Int) {
  if (Build.VERSION.SDK_INT >= 26) {
    vibrate(VibrationEffect.createWaveform(timings, repeat))
  } else {
    @Suppress("DEPRECATION")
    vibrate(timings, repeat)
  }
}
