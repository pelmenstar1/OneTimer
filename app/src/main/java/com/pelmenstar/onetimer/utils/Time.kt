package com.pelmenstar.onetimer.utils

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
