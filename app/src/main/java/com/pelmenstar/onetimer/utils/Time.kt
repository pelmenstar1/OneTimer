package com.pelmenstar.onetimer.utils

import android.content.Context
import android.text.format.DateFormat
import java.util.Date

const val MS_IN_SECOND = 1000
const val MS_IN_MINUTE = 60 * MS_IN_SECOND

private fun StringBuilder.appendTwoDigits(value: Int) {
  if (value < 10) {
    append('0')
  }

  append(value)
}

fun formatTime(minutes: Int): String {
  val hours = minutes / 60
  val minutes = minutes - hours * 60

  return buildString(5) {
    appendTwoDigits(hours)
    append(':')
    appendTwoDigits(minutes)
  }
}

fun formatTimeFromWallTime(context: Context, timeMs: Long): String {
  return DateFormat.getTimeFormat(context).format(Date(timeMs))
}
