package com.pelmenstar.onetimer.external.alarm

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.core.net.toUri

class SettingsFullScreenIntentContract : ActivityResultContract<Unit, Unit>() {
  @RequiresApi(34)
  override fun createIntent(
    context: Context,
    input: Unit
  ): Intent {
    return Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
      data = "package:com.pelmenstar.onetimer".toUri()
    }
  }

  // The settings screen reports no result, the permission has to be re-read afterwards.
  override fun parseResult(resultCode: Int, intent: Intent?) = Unit
}
