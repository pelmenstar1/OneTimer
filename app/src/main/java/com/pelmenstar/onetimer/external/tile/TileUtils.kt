package com.pelmenstar.onetimer.external.tile

import android.service.quicksettings.TileService

inline fun TileService.runWhenUnlocked(crossinline block: () -> Unit) {
  if (isLocked) {
    unlockAndRun { block() }
  } else {
    block()
  }
}
