package com.pelmenstar.onetimer.external.tile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.pelmenstar.onetimer.R
import com.pelmenstar.onetimer.activities.main.MainActivity
import com.pelmenstar.onetimer.flow.AlarmFlow
import com.pelmenstar.onetimer.persistance.ActiveAlarmInfo
import com.pelmenstar.onetimer.utils.formatTimeFromWallTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Asks the system to call [AlarmTileService.onStartListening], so that the tile can refresh itself
 * after the alarm is changed from somewhere else.
 */
fun requestAlarmTileUpdate(context: Context) {
  TileService.requestListeningState(
    context, ComponentName(context, AlarmTileService::class.java)
  )
}

private const val DEFAULT_MINUTES = 60

class AlarmTileService : TileService() {
  private val scope =
    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

  override fun onStartListening() {
    super.onStartListening()
    val context = this

    scope.launch {
      updateTile(AlarmFlow.getActive(context))
    }
  }

  override fun onClick() {
    super.onClick()
    val context = this

    scope.launch {
      if (AlarmFlow.hasActiveAlarm(context)) {
        AlarmFlow.clear(context)

        updateTile(null)
      } else {
        val info = AlarmFlow.schedule(context, DEFAULT_MINUTES)

        if (info != null) {
          updateTile(info)
        } else {
          // Exact alarms are not allowed, the permission can only be granted from the app.
          openApp()
        }
      }
    }
  }

  override fun onDestroy() {
    scope.cancel()

    super.onDestroy()
  }

  private fun updateTile(info: ActiveAlarmInfo?) {
    // qsTile is null when the tile is not listening anymore.
    val tile = qsTile ?: return

    if (info == null) {
      tile.state = Tile.STATE_INACTIVE

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        tile.subtitle = getString(R.string.tile_alarm_subtitle_not_set)
      }
    } else {
      tile.state = Tile.STATE_ACTIVE

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        tile.subtitle = formatTimeFromWallTime(this, info.targetTime)
      }
    }

    tile.updateTile()
  }

  private fun openApp() {
    runWhenUnlocked { startMainActivity() }
  }

  @SuppressLint("StartActivityAndCollapseDeprecated")
  private fun startMainActivity() {
    val intent = Intent(this, MainActivity::class.java)

    if (Build.VERSION.SDK_INT >= 34) {
      val pendingIntent = PendingIntent.getActivity(
        this,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      startActivityAndCollapse(pendingIntent)
    } else {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

      @Suppress("DEPRECATION")
      startActivityAndCollapse(intent)
    }
  }
}
