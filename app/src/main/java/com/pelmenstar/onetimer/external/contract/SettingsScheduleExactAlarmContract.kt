package com.pelmenstar.onetimer.external.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi

class SettingsScheduleExactAlarmContract :
  ActivityResultContract<Unit, Boolean>() {
  @RequiresApi(31)
  override fun createIntent(
    context: Context,
    input: Unit
  ): Intent {
    return Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
      data = packageUri()
    }
  }

  override fun parseResult(
    resultCode: Int,
    intent: Intent?
  ): Boolean {
    return resultCode == Activity.RESULT_OK
  }
}
